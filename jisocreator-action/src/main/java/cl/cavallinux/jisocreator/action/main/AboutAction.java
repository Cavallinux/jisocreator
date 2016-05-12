package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.gui.dialog.AboutDialog;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.util.ImageUtils;


public class AboutAction extends Action {
    private static AboutAction instance;

    static {
	instance = new AboutAction();
    }

    public AboutAction() {
	super("About JIsoCreator", ImageUtils.getInstance().loadImageDescriptor("about.png"));
	setToolTipText("Open about dialog");
    }

    @Override
    public void run() {
	AboutDialog dialog = new AboutDialog(MainWindow.getInstance().getShell());
	dialog.open();
	dialog.close();
    }

    public static AboutAction getInstance() {
	return instance;
    }
}