package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.util.ImageUtils;

public class ExitApplicationAction extends Action {
    private static ExitApplicationAction instance;

    private ExitApplicationAction() {
	super("Exit");
	setToolTipText("Create new iso layout");
	setImageDescriptor(ImageUtils.getInstance().loadImageDescriptor("exit.png"));
    }

    @Override
    public void run() {
	MessageBox exitMessage = new MessageBox(MainWindow.getInstance().getShell(),
		SWT.YES | SWT.NO | SWT.ICON_QUESTION);
	exitMessage.setText("JIsoCreator");
	exitMessage.setMessage("Are you sure to exit JIsocreator?");
	switch (exitMessage.open()) {
	case SWT.YES:
	    MainWindow.getInstance().getShell().setVisible(false);
	    MainWindow.getInstance().close();
	    Display.getCurrent().dispose();
	    System.exit(0);
	default:
	    return;
	}
    }

    public static ExitApplicationAction getInstance() {
	if (instance == null) {
	    instance = new ExitApplicationAction();
	}
	return instance;
    }
}