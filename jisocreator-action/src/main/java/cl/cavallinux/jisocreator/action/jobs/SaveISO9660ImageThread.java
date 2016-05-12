package cl.cavallinux.jisocreator.action.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swt.widgets.Display;

import cl.cavallinux.jisocreator.gui.window.MainWindow;

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
		    ModalContext.run(SaveISO9660ImageThread.this, true, MainWindow.getInstance().getStatusLine().getProgressMonitor(), Display.getCurrent());
		} catch (InvocationTargetException e) {
		    e.printStackTrace();
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
	try {
	    monitor.beginTask("Saving image...", IProgressMonitor.UNKNOWN);
	    InputStreamReader isr = new InputStreamReader(saveProgressInputStream);
	    LineNumberReader lnr = new LineNumberReader(isr);
	    String line;
	    while ((line = lnr.readLine()) != null) {
		monitor.subTask(line);
		System.out.println(line);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    monitor.done();
	}
    }
}