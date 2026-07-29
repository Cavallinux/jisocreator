package cl.cavallinux.jisocreator.gui.i18n;

import org.eclipse.osgi.util.NLS;

/**
 * NLS messages for {@link cl.cavallinux.jisocreator.gui.dialog.QuestionWithToggleDialog}.
 */
public class QuestionWithToggleDialogMessages extends NLS implements INLSBundleMessages {
    public static String questionDialogYesButtonLabel;
    public static String questionDialogNoButtonLabel;
    public static String exitConfirmDialogTitle;
    public static String exitConfirmDialogMessage;
    public static String exitConfirmDialogToggleMessage;

    static {
        initializeMessages(QUESTIONWITHTOGGLEDIALOG_BUNDLE_MESSAGE, QuestionWithToggleDialogMessages.class);
    }
}
