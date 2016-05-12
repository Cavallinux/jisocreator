package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceManager;

import cl.cavallinux.jisocreator.gui.dialog.PreferencesDialog;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.util.ImageUtils;

public class PreferencesAction extends Action {
    private static PreferencesAction instance;

    public PreferencesAction() {
	super("Preferences", ImageUtils.getInstance().loadImageDescriptor("preferences.png"));
	setToolTipText("Open preferences dialog");
    }

    @Override
    public void run() {
	PreferencesDialog dialog = new PreferencesDialog(MainWindow.getInstance().getShell(), new PreferenceManager());
	dialog.open();
	dialog.close();
    }

    public static PreferencesAction getInstance() {
	if (instance == null) {
	    instance = new PreferencesAction();
	}
	return instance;
    }
}