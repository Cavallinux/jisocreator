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

import cl.cavallinux.jisocreator.instances.GUIManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class SaveISO9660ImageThread extends Thread implements IRunnableWithProgress {
    private InputStream saveProgressInputStream;
    private boolean commandLineMode;

    public SaveISO9660ImageThread(InputStream saveProgressInputStream, boolean commandLineMode) {
        super("cl.cavallinux.jisocreator.iso.save.thread");
        this.saveProgressInputStream = saveProgressInputStream;
        this.commandLineMode = commandLineMode;
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
    
    private void printGUIMode() {
        Display.getDefault().asyncExec(new Thread(() -> {
            try {
                IProgressMonitor progressMonitor = GUIManager.INSTANCE.getMainWindow().getProgressMonitor();
                ModalContext.run(SaveISO9660ImageThread.this, true, progressMonitor, Display.getCurrent());
            } catch (InvocationTargetException | InterruptedException e) {
                log.error("Error saving ISO image", e);
            }
        }));
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
}