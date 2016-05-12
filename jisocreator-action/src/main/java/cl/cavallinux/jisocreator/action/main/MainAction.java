package cl.cavallinux.jisocreator.action.main;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.gui.window.MainWindow;

public class MainAction extends Action {
    private static MainAction instance = null;

    @Override
    public void run() {
	MainWindow.getInstance().setBlockOnOpen(true);
	MainWindow.getInstance().open();
    }

    public static void main(String[] args) {
	PropertyConfigurator.configure(System.getProperty("log4j.file"));
	getInstance().run();
    }

    private synchronized static void createInstance() {
	instance = new MainAction();
    }

    public static MainAction getInstance() {
	if (instance == null) {
	    createInstance();
	}
	return instance;
    }
}