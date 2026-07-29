package cl.cavallinux.jisocreator.gui.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Composite;
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
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setText(AddToISODialogMessages.addToIsoDialogOKButtonLabel);
        getButton(IDialogConstants.CANCEL_ID).setText(AddToISODialogMessages.addToIsoDialogOKCancelLabel);
    }
}
