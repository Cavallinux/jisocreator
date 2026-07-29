package cl.cavallinux.jisocreator.gui.theme;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Shell;

public final class DarkJFaceDialogs {
    private DarkJFaceDialogs() {
    }

    public static int openError(Shell parentShell, String title, String message) {
        MessageDialog dialog = new MessageDialog(parentShell, title, null, message, MessageDialog.ERROR,
                new String[] { IDialogConstants.OK_LABEL }, 0);
        return openWithDarkTheme(dialog);
    }

    public static MessageDialogWithToggle openYesNoQuestionWithToggle(Shell parentShell, String title, String message,
            String toggleMessage, boolean toggleState) {
        MessageDialogWithToggle dialog = new MessageDialogWithToggle(parentShell, title, null, message,
                MessageDialog.QUESTION, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 0,
                toggleMessage, toggleState);
        openWithDarkTheme(dialog);
        return dialog;
    }

    private static int openWithDarkTheme(MessageDialog dialog) {
        dialog.create();
        if (dialog.getShell() != null && !dialog.getShell().isDisposed()) {
            DarkThemeSupport.enableWindowsDarkMode(dialog.getShell().getDisplay());
            DarkThemeSupport.applyToControlTree(dialog.getShell());
        }
        return dialog.open();
    }
}
