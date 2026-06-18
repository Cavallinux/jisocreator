package cl.cavallinux.jisocreator.action.main;

import java.util.Arrays;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;

import cl.cavallinux.jisocreator.gui.dialog.JISOCreatorPreferencesDialog;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.instances.PreferencesNodeManager;

public class PreferencesAction extends Action {

    public PreferencesAction() {
        super("Preferences", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("preferences.png"));
        setToolTipText("Open preferences dialog");
    }

    @Override
    public void run() {
        PreferenceDialog dialog = new JISOCreatorPreferencesDialog(GUIManager.INSTANCE.getMainWindow().getShell(),
                createPreferenceManager());
        dialog.setPreferenceStore(IOManager.INSTANCE.getIoUtils().getStore());
        dialog.open();
        dialog.close();
    }

    private PreferenceManager createPreferenceManager() {
        PreferenceManager preferenceManager = new PreferenceManager();
        Arrays.asList(PreferencesNodeManager.values()).forEach(preferenceNodeManager -> {
            preferenceManager.addToRoot(preferenceNodeManager.getPreferenceNode());
        });
        return preferenceManager;
    }
}