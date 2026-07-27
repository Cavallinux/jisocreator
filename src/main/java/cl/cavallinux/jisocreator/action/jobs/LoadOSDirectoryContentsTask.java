package cl.cavallinux.jisocreator.action.jobs;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;
import lombok.extern.slf4j.Slf4j;

/**
 * Background task that pre-fetches file metadata for a directory selected in
 * the OS file-system explorer before it is displayed in the table.
 * <p>
 * Populating the OS directories table (via {@link TableViewer#setInput(Object)})
 * synchronously on the SWT UI thread requires, for every visible row, several
 * filesystem stat calls (directory check, size, last-modified time) performed by
 * {@link OSExplorer}. For directories containing a large number of entries, doing
 * all of this work synchronously in response to a tree selection blocks the UI
 * thread and makes the application feel unresponsive.
 * </p>
 * <p>
 * This task instead performs the expensive filesystem scan
 * ({@link OSExplorer#warmAttributesCache(java.nio.file.Path)}, a plain
 * {@code java.nio} operation with no SWT/native dependency) off the UI thread on
 * a shared single-thread {@link ExecutorService}, and only marshals the actual
 * {@code setInput(...)} call back onto the UI thread (via
 * {@link Display#asyncExec(Runnable)}, as required by SWT's single-threaded
 * widget access rule) once the metadata is already warmed in {@link OSExplorer}'s
 * attribute cache — making that call much cheaper than an unprepared synchronous
 * load.
 * </p>
 * <p>
 * If the user navigates to a different directory before a previously submitted
 * task finishes, its stale result is discarded: only the most recently
 * requested directory is applied to the table. In addition, {@link #submit()}
 * cancels the previously submitted {@link Future} before enqueueing the new
 * one: since the shared executor is single-threaded, a still-queued (not yet
 * started) task is removed from the queue entirely and never runs, so rapid
 * directory navigation does not accumulate wasted background scans. A task
 * that has already started running when superseded is left to finish on its
 * own — {@code cancel(false)} does not interrupt it — but its result is still
 * discarded via the same "still latest" check.
 * </p>
 * <p>
 * While a task is pending or running, the {@code Shell} owning the table
 * displays the platform's {@link SWT#CURSOR_WAIT} busy cursor (a shared system
 * cursor, requiring no disposal by client code), so the user gets immediate
 * visual feedback that the directory is loading instead of a silently
 * unresponsive UI. The cursor is restored to its default as soon as the
 * winning (still-latest) request finishes applying its result — a superseded
 * task simply skips restoring the cursor, leaving that responsibility to
 * whichever request is the latest one.
 * </p>
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.2.3
 * @since 0.2.3
 */
@Slf4j
public class LoadOSDirectoryContentsTask implements Runnable {

    /**
     * Single-thread executor shared by every {@link LoadOSDirectoryContentsTask}:
     * directory loads only ever need to happen one at a time (only one directory
     * listing is visible in the OS explorer table at a time), so a single
     * background thread is reused across selections instead of spawning a new
     * {@code Thread} per navigation.
     */
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "cl.cavallinux.jisocreator.osexplorer.load.directory.thread");
        thread.setDaemon(true);
        return thread;
    });

    private static final AtomicReference<File> LATEST_REQUESTED_DIRECTORY = new AtomicReference<>();
    private static final AtomicReference<Future<?>> PENDING_TASK = new AtomicReference<>();

    private final File directory;
    private final TableViewer tableViewer;

    @lombok.Builder
    private LoadOSDirectoryContentsTask(File directory, TableViewer tableViewer) {
        this.directory = directory;
        this.tableViewer = tableViewer;
        LATEST_REQUESTED_DIRECTORY.set(directory);
    }

    /**
     * Shows the busy cursor immediately, cancels any previously submitted task
     * that has not started running yet (discarding it from the executor's queue
     * before it wastes any work), and submits this task to the shared
     * single-thread executor.
     */
    public void submit() {
        showBusyCursor();
        Future<?> previousTask = PENDING_TASK.getAndSet(EXECUTOR.submit(this));
        if (previousTask != null) {
            previousTask.cancel(false);
        }
    }

    @Override
    public void run() {
        log.info("Pre-fetching file metadata in background for directory: {}", directory);
        try {
            OSExplorer osExplorer = OSAndIsoExplorerManager.INSTANCE.getOsExplorer();
            osExplorer.warmAttributesCache(directory.toPath());
        } catch (RuntimeException e) {
            // Still fall through to restore the busy cursor (via applyToTableViewer
            // below) even if pre-fetching failed, so the UI never gets stuck showing
            // a wait cursor indefinitely.
            log.warn("Error pre-fetching file metadata for directory: {}", directory, e);
        }
        if (!isStillLatestRequest()) {
            log.debug("Discarding stale directory-load result for {}: a newer selection superseded it", directory);
            return;
        }
        Display display = Display.getDefault();
        if (display != null && !display.isDisposed()) {
            display.asyncExec(this::applyToTableViewer);
        }
    }

    private void applyToTableViewer() {
        if (!tableViewer.getControl().isDisposed() && isStillLatestRequest()) {
            tableViewer.setInput(directory);
            restoreDefaultCursor();
        }
    }

    /**
     * Switches the cursor of the {@code Shell} owning {@link #tableViewer} to the
     * platform's busy/wait cursor, providing immediate feedback that a directory
     * is loading. A no-op when {@link #tableViewer} is {@code null} or already
     * disposed (e.g. when this task is only used to exercise its pure
     * {@link #isStillLatestRequest()} logic in tests, without a real control).
     */
    private void showBusyCursor() {
        Control control = obtainControlIfUsable();
        if (control == null) {
            return;
        }
        Shell shell = control.getShell();
        shell.setCursor(shell.getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
    }

    /**
     * Restores the default cursor on the {@code Shell} owning {@link #tableViewer},
     * undoing {@link #showBusyCursor()} once the directory contents have been
     * applied to the table.
     */
    private void restoreDefaultCursor() {
        Control control = obtainControlIfUsable();
        if (control == null) {
            return;
        }
        control.getShell().setCursor(null);
    }

    private Control obtainControlIfUsable() {
        if (tableViewer == null) {
            return null;
        }
        Control control = tableViewer.getControl();
        return (control == null || control.isDisposed()) ? null : control;
    }

    /**
     * Checks whether the directory this task was created for is still the most
     * recently requested one, i.e. whether no other {@link LoadOSDirectoryContentsTask}
     * has been created for a different directory since this one was submitted.
     * 
     * @return true if this task's result should still be applied, false if it
     *         has been superseded by a more recent directory selection
     */
    boolean isStillLatestRequest() {
        return directory.equals(LATEST_REQUESTED_DIRECTORY.get());
    }
}
