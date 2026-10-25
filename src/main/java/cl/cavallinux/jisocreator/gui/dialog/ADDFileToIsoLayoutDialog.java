package cl.cavallinux.jisocreator.gui.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import cl.cavallinux.jisocreator.gui.i18n.AddToISODialogMessages;
import cl.cavallinux.jisocreator.gui.theme.DarkThemeSupport;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ADDFileToIsoLayoutDialog extends ElementTreeSelectionDialog {
    
    @Builder
    public ADDFileToIsoLayoutDialog(Shell parent, IBaseLabelProvider labelProvider,
            ITreeContentProvider contentProvider) {
        super(parent, labelProvider, contentProvider);
    }
    
    @Override
    protected Label createMessageArea(Composite composite) {
        log.info("Creating dialog message area");
        setMessage(AddToISODialogMessages.addToIsoDialogStaticInfo);
        return super.createMessageArea(composite);
    }
    
    @Override
    protected void configureShell(Shell shell) {
        log.info("Configuring dialog shell");
        super.configureShell(shell);
        DarkThemeSupport.enableWindowsDarkMode(shell.getDisplay());
        shell.setText(AddToISODialogMessages.addToIsoDialogWindowTitle);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        log.info("Creating add file to iso layout dialog area components");
        Control dialogArea = super.createDialogArea(parent);
        DarkThemeSupport.applyToControlTree(dialogArea);
        return dialogArea;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        /**
         * ElementTreeSelectionDialog extends SelectionStatusDialog, which extends
         * TrayDialog. TrayDialog wraps the button-bar area created by
         * Dialog.createButtonBar(...) inside its OWN outer composite (see
         * TrayDialog#createButtonBar: it creates a wrapper Composite, optionally adds a
         * help control, then delegates to super.createButtonBar(wrapper)). The `parent`
         * received by createButtonsForButtonBar(...) below is only that inner
         * button-bar composite, so styling it alone leaves the TrayDialog wrapper
         * unstyled with its default light background (same fix already applied to
         * JISOCreatorPreferencesDialog, the other TrayDialog subclass in this
         * codebase). Applying the dark theme here, on the full Control returned by
         * super.createButtonBar(...), covers the outer wrapper as well.
         */
        Control buttonBar = super.createButtonBar(parent);
        DarkThemeSupport.applyToControlTree(buttonBar);
        return buttonBar;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setText(AddToISODialogMessages.addToIsoDialogOKButtonLabel);
        getButton(IDialogConstants.CANCEL_ID).setText(AddToISODialogMessages.addToIsoDialogOKCancelLabel);
        DarkThemeSupport.applyToControlTree(parent);
    }
}
