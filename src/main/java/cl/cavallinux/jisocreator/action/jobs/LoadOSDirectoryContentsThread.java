package cl.cavallinux.jisocreator.action.jobs;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;

import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;
import lombok.extern.slf4j.Slf4j;

/**
 * Background thread that pre-fetches file metadata for a directory selected in
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
 * This thread instead performs the expensive filesystem scan
 * ({@link OSExplorer#warmAttributesCache(java.nio.file.Path)}, a plain
 * {@code java.nio} operation with no SWT/native dependency) off the UI thread,
 * and only marshals the actual {@code setInput(...)} call back onto the UI
 * thread (via {@link Display#asyncExec(Runnable)}, as required by SWT's
 * single-threaded widget access rule) once the metadata is already warmed in
 * {@link OSExplorer}'s attribute cache — making that call much cheaper than an
 * unprepared synchronous load.
 * </p>
 * <p>
 * If the user navigates to a different directory before a previously started
 * background load finishes, the stale result is discarded: only the most
 * recently requested directory is applied to the table.
 * </p>
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.2.3
 * @since 0.2.3
 */
@Slf4j
public class LoadOSDirectoryContentsThread extends Thread {

    private static final AtomicReference<File> LATEST_REQUESTED_DIRECTORY = new AtomicReference<>();

    private final File directory;
    private final TableViewer tableViewer;

    @lombok.Builder
    private LoadOSDirectoryContentsThread(File directory, TableViewer tableViewer) {
        super("cl.cavallinux.jisocreator.osexplorer.load.directory.thread");
        this.directory = directory;
        this.tableViewer = tableViewer;
        LATEST_REQUESTED_DIRECTORY.set(directory);
    }

    @Override
    public void run() {
        log.info("Pre-fetching file metadata in background for directory: {}", directory);
        OSExplorer osExplorer = OSAndIsoExplorerManager.INSTANCE.getOsExplorer();
        osExplorer.warmAttributesCache(directory.toPath());
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
        }
    }

    /**
     * Checks whether the directory this thread was created for is still the most
     * recently requested one, i.e. whether no other {@link LoadOSDirectoryContentsThread}
     * has been created for a different directory since this one started.
     * 
     * @return true if this thread's result should still be applied, false if it
     *         has been superseded by a more recent directory selection
     */
    boolean isStillLatestRequest() {
        return directory.equals(LATEST_REQUESTED_DIRECTORY.get());
    }
}
