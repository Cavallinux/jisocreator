package cl.cavallinux.jisocreator.gui.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import cl.cavallinux.jisocreator.gui.i18n.PreferenceDialogMessages;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class JISOCreatorPreferencesDialog extends PreferenceDialog {
    @Builder
    protected JISOCreatorPreferencesDialog(Shell parentShell, PreferenceManager manager) {
        super(parentShell, manager);
        log.info("Initializing JISOCreatorPreferencesDialog");
    }

    @Override
    protected void configureShell(Shell newShell) {
        log.info("Configuring shell for JISOCreatorPreferencesDialog");
        super.configureShell(newShell);
        newShell.setText(PreferenceDialogMessages.preferenceDialogWindowTitle);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setText(PreferenceDialogMessages.preferenceDialogOKButton);
        getButton(IDialogConstants.CANCEL_ID).setText(PreferenceDialogMessages.preferenceDialogCancelButton);
    }
}