package cl.cavallinux.jisocreator.action.jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swt.widgets.Display;

import cl.cavallinux.jisocreator.gui.window.MainWindow;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SaveISO9660ImageThread extends Thread implements IRunnableWithProgress {
    private InputStream saveProgressInputStream;

    public SaveISO9660ImageThread(InputStream saveProgressInputStream) {
        super("cl.cavallinux.jisocreator.iso.save.thread");
        this.saveProgressInputStream = saveProgressInputStream;
    }

    @Override
    public void run() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    ModalContext.run(SaveISO9660ImageThread.this, true,
                            MainWindow.getInstance().getStatusLine().getProgressMonitor(), Display.getCurrent());
                } catch (InvocationTargetException | InterruptedException e) {
                    log.error("Error saving ISO image", e);
                }
            }
        });
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(saveProgressInputStream))) {
            monitor.beginTask("Saving image...", IProgressMonitor.UNKNOWN);
            br.lines().forEach(line -> {
                monitor.subTask(line);
                log.info(line);
            });
        } catch (IOException e) {
            log.error("Error reading save progress", e);
        } finally {
            monitor.done();
        }
    }
}