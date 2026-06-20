package cl.cavallinux.jisocreator.action.jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swt.widgets.Display;

import cl.cavallinux.jisocreator.instances.GUIManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class SaveISO9660ImageThread extends Thread implements IRunnableWithProgress {
    private static final Pattern LOG_PATTERN = Pattern.compile("([\\d,]+)% done, estimate finish (.+)");
    private static final Integer SUCCESSFULLY_ISO_IMAGE_CREATION_PROGRESS = 100;
    private static final Integer INITIAL_ISO_IMAGE_CREATION_PROGRESS = 0;
    private InputStream saveProgressInputStream;
    private Process mkisofsProcess;
    private boolean commandLineMode;

    @lombok.Builder
    private SaveISO9660ImageThread() {
        super("cl.cavallinux.jisocreator.iso.save.thread");
    }

    @Override
    public void run() {
        if (commandLineMode) {
            printCommandLineMode();
        } else {
            printGUIMode();
        }
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        AtomicInteger reportedProgress = new AtomicInteger(INITIAL_ISO_IMAGE_CREATION_PROGRESS);
        monitor.beginTask("Saving image...", SUCCESSFULLY_ISO_IMAGE_CREATION_PROGRESS);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(saveProgressInputStream))) {
            br.lines().takeWhile(line -> !monitor.isCanceled()).forEach(line -> {
                log.info(line);
                String ellapsedPercentage = extractEllapsedPercentage(line);
                if (StringUtils.isNotBlank(ellapsedPercentage)) {
                    Double progress = Double.parseDouble(ellapsedPercentage.replace(',', '.'));
                    Integer currentProgress = progress.intValue();
                    int deltaWork = currentProgress - reportedProgress.get();
                    if (deltaWork > 0) {
                        monitor.worked(deltaWork);
                        reportedProgress.set(currentProgress);
                    }
                    monitor.subTask(line);
                }
            });
            verifyCanceledProcess(monitor);
        } catch (IOException e) {
            log.error("Error reading save progress", e);
            killISOSavingProcess();
        } finally {
            Display.getDefault().asyncExec(() -> {
                GUIManager.INSTANCE.getMainWindow().setStatusLineActiveCancelButton(false);
            });
            monitor.done();
        }
    }

    private void verifyCanceledProcess(IProgressMonitor monitor) {
        if (monitor.isCanceled()) {
            killISOSavingProcess();
        }
    }

    private void killISOSavingProcess() {
        if (Objects.nonNull(mkisofsProcess) && mkisofsProcess.isAlive()) {
            log.info("Killing OS saving ISO process");
            mkisofsProcess.destroy();
            log.info("Process successfully killed!");
        }
    }

    private void printGUIMode() {
        Display.getDefault().asyncExec(() -> {
            try {
                IProgressMonitor progressMonitor = GUIManager.INSTANCE.getMainWindow().getProgressMonitor();
                GUIManager.INSTANCE.getMainWindow().setStatusLineActiveCancelButton(true);
                ModalContext.run(SaveISO9660ImageThread.this, true, progressMonitor, Display.getCurrent());
            } catch (InvocationTargetException | InterruptedException e) {
                log.error("Error saving iso image", e);
            }
        });
    }

    private void printCommandLineMode() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(saveProgressInputStream))) {
            System.out.println("Saving image");
            br.lines().forEach(line -> {
                System.out.println(line);
            });
        } catch (IOException e) {
            log.error("Error reading save progress", e);
        }
    }

    private String extractEllapsedPercentage(String line) {
        Matcher matcher = LOG_PATTERN.matcher(line);
        return matcher.find() ? matcher.group(1) : StringUtils.EMPTY;
    }
}