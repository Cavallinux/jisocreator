package cl.cavallinux.jisocreator.gui.theme;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import cl.cavallinux.jisocreator.gui.dialog.ErrorMessageDialog;
import cl.cavallinux.jisocreator.gui.dialog.QuestionWithToggleDialog;
import cl.cavallinux.jisocreator.gui.i18n.QuestionWithToggleDialogMessages;
import cl.cavallinux.jisocreator.instances.ImageRegister;

public final class DarkJFaceDialogs {
    private DarkJFaceDialogs() {
    }

    public static int openError(Shell parentShell, String title, String message) {
        return openError(parentShell, title, message, null);
    }

    /**
     * Same as {@link #openError(Shell, String, String)}, but allows a custom
     * icon to be shown next to the error message instead of the standard
     * system error icon.
     *
     * @param parentShell the parent shell
     * @param title       the dialog title
     * @param message     the dialog message
     * @param messageIcon a custom icon shown next to the message, or {@code null} to use the
     *                    standard system error icon
     * @return the dialog return code
     */
    public static int openError(Shell parentShell, String title, String message, Image messageIcon) {
        MessageDialog dialog = new ErrorMessageDialog(parentShell, title, null, message,
                new String[] { IDialogConstants.OK_LABEL }, 0, messageIcon);
        return openWithDarkTheme(dialog);
    }

    public static MessageDialogWithToggle openYesNoQuestionWithToggle(Shell parentShell, String title, String message,
            String toggleMessage, boolean toggleState) {
        return openYesNoQuestionWithToggle(parentShell, title, message, toggleMessage, toggleState,
                QuestionWithToggleDialogMessages.questionDialogYesButtonLabel,
                QuestionWithToggleDialogMessages.questionDialogNoButtonLabel);
    }

    /**
     * Same as {@link #openYesNoQuestionWithToggle(Shell, String, String, String, boolean)}, but
     * allows the Yes/No button labels to be customized instead of using the
     * default, translated {@link QuestionWithToggleDialogMessages} labels.
     *
     * @param parentShell    the parent shell
     * @param title          the dialog title
     * @param message        the dialog message
     * @param toggleMessage  the message for the toggle control, or {@code null} for the default message
     * @param toggleState    the initial state of the toggle
     * @param yesButtonLabel the text for the affirmative button
     * @param noButtonLabel  the text for the negative button
     * @return the opened {@link MessageDialogWithToggle}
     */
    public static MessageDialogWithToggle openYesNoQuestionWithToggle(Shell parentShell, String title, String message,
            String toggleMessage, boolean toggleState, String yesButtonLabel, String noButtonLabel) {
        return openYesNoQuestionWithToggle(parentShell, title, message, toggleMessage, toggleState, yesButtonLabel,
                noButtonLabel, null);
    }

    /**
     * Same as
     * {@link #openYesNoQuestionWithToggle(Shell, String, String, String, boolean, String, String)},
     * but allows a custom icon to be shown next to the confirmation message
     * instead of the standard system question icon.
     *
     * @param parentShell    the parent shell
     * @param title          the dialog title
     * @param message        the dialog message
     * @param toggleMessage  the message for the toggle control, or {@code null} for the default message
     * @param toggleState    the initial state of the toggle
     * @param yesButtonLabel the text for the affirmative button
     * @param noButtonLabel  the text for the negative button
     * @param messageIcon    a custom icon shown next to the message, or {@code null} to use the
     *                       standard system question icon
     * @return the opened {@link MessageDialogWithToggle}
     */
    public static MessageDialogWithToggle openYesNoQuestionWithToggle(Shell parentShell, String title, String message,
            String toggleMessage, boolean toggleState, String yesButtonLabel, String noButtonLabel,
            Image messageIcon) {
        MessageDialogWithToggle dialog = new QuestionWithToggleDialog(parentShell, title, null, message,
                MessageDialog.QUESTION, yesButtonLabel, noButtonLabel, 0, toggleMessage, toggleState, messageIcon);
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
