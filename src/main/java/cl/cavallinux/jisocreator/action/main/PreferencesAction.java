package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;

import cl.cavallinux.jisocreator.gui.dialog.JISOCreatorPreferencesDialog;
import cl.cavallinux.jisocreator.gui.preference.GeneralPreferencesPage;
import cl.cavallinux.jisocreator.gui.preference.MKISOFSPreferencePage;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;

public class PreferencesAction extends Action {

    public PreferencesAction() {
        super("Preferences", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("preferences.png"));
        setToolTipText("Open preferences dialog");
    }

    @Override
    public void run() {
        JISOCreatorPreferencesDialog dialog = new JISOCreatorPreferencesDialog(
                GUIManager.INSTANCE.getMainWindow().getShell(), createPreferenceManager());
        dialog.setPreferenceStore(IOManager.INSTANCE.getIoUtils().getStore());
        dialog.open();
        dialog.close();
    }

    private PreferenceManager createPreferenceManager() {
        PreferenceManager preferenceManager = new PreferenceManager();
        //preferenceManager.removeAll();
        preferenceManager.addToRoot(new PreferenceNode("preferences.mkisofs", "ISO options",
                ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("iso.png"),
                MKISOFSPreferencePage.class.getName()));
        preferenceManager.addToRoot(new PreferenceNode("preferences.general", "General options",
                ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("iso.png"),
                GeneralPreferencesPage.class.getName()));
        return preferenceManager;
    }
}