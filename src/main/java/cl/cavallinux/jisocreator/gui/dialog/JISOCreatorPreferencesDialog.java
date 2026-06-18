package cl.cavallinux.jisocreator.gui.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import cl.cavallinux.jisocreator.gui.preference.GeneralPreferencesPage;
import cl.cavallinux.jisocreator.gui.preference.MKISOFSPreferencePage;
import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JISOCreatorPreferencesDialog extends PreferenceDialog {
    public JISOCreatorPreferencesDialog(Shell parentShell, PreferenceManager manager) {
        super(parentShell, manager);
        log.info("Initializing JISOCreatorPreferencesDialog");
        getPreferenceManager().removeAll();
        getPreferenceManager().addToRoot(new PreferenceNode("preferences.mkisofs", "ISO options",
                ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("iso.png"),
                MKISOFSPreferencePage.class.getName()));
        getPreferenceManager().addToRoot(new PreferenceNode("preferences.general", "General options",
                ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("iso.png"),
                GeneralPreferencesPage.class.getName()));
        setPreferenceStore(IOManager.INSTANCE.getIoUtils().getStore());
    }

    @Override
    protected void configureShell(Shell newShell) {
        log.info("Configuring shell for JISOCreatorPreferencesDialog");
        super.configureShell(newShell);
        newShell.setText("JIsocreator Preferences");
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setText("Accept");
        getButton(IDialogConstants.CANCEL_ID).setText("Cancel");
    }
}