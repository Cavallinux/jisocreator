package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceManager;

import cl.cavallinux.jisocreator.gui.dialog.PreferencesDialog;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;

public class PreferencesAction extends Action {
    private PreferenceManager preferenceManager;

    public PreferencesAction() {
        super("Preferences", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("preferences.png"));
        setToolTipText("Open preferences dialog");
        preferenceManager = new PreferenceManager();
    }

    @Override
    public void run() {
        PreferencesDialog dialog = new PreferencesDialog(GUIManager.INSTANCE.getMainWindow().getShell(),
                preferenceManager);
        dialog.open();
        dialog.close();
    }
}