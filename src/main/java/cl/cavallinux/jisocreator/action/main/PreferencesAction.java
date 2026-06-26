package cl.cavallinux.jisocreator.action.main;

import java.util.Arrays;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.widgets.Shell;

import cl.cavallinux.jisocreator.gui.dialog.JISOCreatorPreferencesDialog;
import cl.cavallinux.jisocreator.gui.i18n.MainActionsMessages;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.instances.PreferencesNodeManager;
import lombok.Builder;

public class PreferencesAction extends Action {
    
    @Builder
    protected PreferencesAction() {
        super(MainActionsMessages.preferencesActionName,
                ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("preferences.png"));
        setToolTipText(MainActionsMessages.preferencesActionTooltip);
    }

    @Override
    public void run() {
        Shell parentShell = GUIManager.INSTANCE.getMainWindow().getShell();
        PreferenceDialog dialog = JISOCreatorPreferencesDialog.builder().manager(createPreferenceManager())
                .parentShell(parentShell).build();
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