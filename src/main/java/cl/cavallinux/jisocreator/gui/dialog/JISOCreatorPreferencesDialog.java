package cl.cavallinux.jisocreator.gui.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import cl.cavallinux.jisocreator.gui.i18n.PreferenceDialogMessages;
import cl.cavallinux.jisocreator.gui.theme.DarkThemeSupport;
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
        DarkThemeSupport.enableWindowsDarkMode(newShell.getDisplay());
        newShell.setText(PreferenceDialogMessages.preferenceDialogWindowTitle);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Control dialogArea = super.createDialogArea(parent);
        DarkThemeSupport.applyToControlTree(dialogArea);
        return dialogArea;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        // PreferenceDialog extends TrayDialog, which wraps the button-bar area created
        // by Dialog.createButtonBar(...) inside its OWN outer composite (see
        // TrayDialog#createButtonBar: it creates a wrapper Composite, optionally adds a
        // help control, then delegates to super.createButtonBar(wrapper)). The `parent`
        // received by createButtonsForButtonBar(...) below is only that inner
        // button-bar composite, so styling it alone leaves the TrayDialog wrapper
        // unstyled. Applying the dark theme here, on the full Control returned by
        // super.createButtonBar(...), covers the outer wrapper as well.
        Control buttonBar = super.createButtonBar(parent);
        DarkThemeSupport.applyToControlTree(buttonBar);
        return buttonBar;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setText(PreferenceDialogMessages.preferenceDialogOKButton);
        getButton(IDialogConstants.CANCEL_ID).setText(PreferenceDialogMessages.preferenceDialogCancelButton);
        DarkThemeSupport.applyToControlTree(parent);
        parent.getDisplay().asyncExec(() -> {
            if (!parent.isDisposed()) {
                DarkThemeSupport.applyToControlTree(parent);
            }
        });
    }
    
    @Override
    protected void okPressed() {
        log.info("OK button pressed in JISOCreatorPreferencesDialog");
        super.okPressed();
    }
    
    @Override
    protected void cancelPressed() {
        log.info("Cancel button pressed in JISOCreatorPreferencesDialog");
        super.cancelPressed();
    }
}