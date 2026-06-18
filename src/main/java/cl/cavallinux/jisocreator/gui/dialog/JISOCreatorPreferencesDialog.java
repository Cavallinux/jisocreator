package cl.cavallinux.jisocreator.gui.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JISOCreatorPreferencesDialog extends PreferenceDialog {
    public JISOCreatorPreferencesDialog(Shell parentShell, PreferenceManager manager) {
        super(parentShell, manager);
        log.info("Initializing JISOCreatorPreferencesDialog");
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