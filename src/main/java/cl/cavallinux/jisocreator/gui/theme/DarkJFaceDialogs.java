package cl.cavallinux.jisocreator.gui.theme;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Shell;

import cl.cavallinux.jisocreator.gui.dialog.QuestionWithToggleDialog;
import cl.cavallinux.jisocreator.instances.ImageRegister;

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
        MessageDialogWithToggle dialog = new QuestionWithToggleDialog(parentShell, title, null, message,
                MessageDialog.QUESTION, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 0,
                toggleMessage, toggleState);
        /**
         * Window#getShell() returns null until create()/open() has actually built the
         * native shell (Window#create() is what assigns the shell field) - calling
         * dialog.getShell().setImage(...) here, before that happens, always threw a
         * NullPointerException. The icon must be applied AFTER create(), which is why
         * it is now passed as the afterCreate hook to openWithDarkTheme(...) below,
         * instead of calling dialog.create() a second time here (Window#create()
         * unconditionally rebuilds the shell on every call, so invoking it twice would
         * leak/duplicate the shell rather than just being a harmless no-op).
         */
        openWithDarkTheme(dialog,
                () -> dialog.getShell().setImage(ImageRegister.INSTANCE.getImageUtils().loadImage("jisocreator.svg")));
        return dialog;
    }

    private static int openWithDarkTheme(MessageDialog dialog) {
        return openWithDarkTheme(dialog, null);
    }

    private static int openWithDarkTheme(MessageDialog dialog, Runnable afterCreate) {
        dialog.create();
        if (dialog.getShell() != null && !dialog.getShell().isDisposed()) {
            DarkThemeSupport.enableWindowsDarkMode(dialog.getShell().getDisplay());
            if (afterCreate != null) {
                afterCreate.run();
            }
            DarkThemeSupport.applyToControlTree(dialog.getShell());
        }
        return dialog.open();
    }
}
