package cl.cavallinux.jisocreator.gui.dialog;

import java.util.LinkedHashMap;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import cl.cavallinux.jisocreator.gui.i18n.QuestionWithToggleDialogMessages;

/**
 * {@link MessageDialogWithToggle} specialization that always shows the
 * standard question icon next to the confirmation message.
 * <p>
 * {@link org.eclipse.jface.dialogs.MessageDialog#init} intentionally leaves
 * the internal image {@code null} for {@code MessageDialog.QUESTION} (and the
 * other question related image types), so
 * {@link org.eclipse.jface.dialogs.IconAndMessageDialog#createMessageArea}
 * never creates the icon {@code Label} for this dialog type. Overriding
 * {@link #getImage()} to fall back to {@link #getQuestionImage()} restores the
 * confirmation icon without altering any other {@code MessageDialogWithToggle}
 * behaviour.
 * </p>
 * <p>
 * The Yes/No button labels are fully parametrized via the
 * {@code yesButtonLabel}/{@code noButtonLabel} constructor arguments, so
 * callers can freely customize them; {@link QuestionWithToggleDialogMessages}
 * additionally supplies English and Spanish translated defaults through the
 * convenience constructor, instead of relying on JFace's own untranslated
 * {@code IDialogConstants.YES_LABEL}/{@code NO_LABEL} constants.
 * </p>
 * <p>
 * Button return codes are explicitly bound via the {@link LinkedHashMap}
 * based {@code MessageDialogWithToggle} constructor, so
 * {@link #getReturnCode()} always yields the standard
 * {@link IDialogConstants#YES_ID}/{@link IDialogConstants#NO_ID} values
 * regardless of label text or button order. Without this, the plain
 * {@code String[]} based constructor lets
 * {@code MessageDialog#createButtonsForButtonBar} fall back to assigning each
 * button its array index as its id, which silently breaks any caller
 * comparing the return code against
 * {@code IDialogConstants.YES_ID}/{@code NO_ID}.
 * </p>
 * <p>
 * An optional custom message icon (shown next to the confirmation text, not
 * to be confused with the {@code titleBarImage} parameter, which only sets
 * the small shell/title-bar icon) can be supplied via the
 * {@code messageIcon} constructor argument; when {@code null}, the standard
 * system question icon is used instead, as before.
 * </p>
 */
public class QuestionWithToggleDialog extends MessageDialogWithToggle {

    private final Image messageIcon;

    /**
     * Creates a question dialog with a toggle, allowing the Yes/No button
     * labels to be freely customized and translated.
     *
     * @param parentShell     the parent shell
     * @param dialogTitle     the dialog title
     * @param titleBarImage   the dialog shell/title-bar image, or {@code null}
     * @param message         the dialog message
     * @param dialogImageType one of the {@link org.eclipse.jface.dialogs.MessageDialog} image type constants
     * @param yesButtonLabel  the text for the affirmative button
     * @param noButtonLabel   the text for the negative button
     * @param defaultIndex    the index of the default button (0 for the yes button, 1 for the no button)
     * @param toggleMessage   the message for the toggle control, or {@code null} for the default message
     * @param toggleState     the initial state of the toggle
     * @param messageIcon     a custom icon shown next to the message, or {@code null} to use the
     *                        standard system question icon
     */
    public QuestionWithToggleDialog(Shell parentShell, String dialogTitle, Image titleBarImage, String message,
            int dialogImageType, String yesButtonLabel, String noButtonLabel, int defaultIndex,
            String toggleMessage, boolean toggleState, Image messageIcon) {
        super(parentShell, dialogTitle, titleBarImage, message, dialogImageType,
                buttonLabelToIdMap(yesButtonLabel, noButtonLabel), defaultIndex, toggleMessage, toggleState);
        this.messageIcon = messageIcon;
    }

    /**
     * Same as the full constructor, without a custom message icon (the
     * standard system question icon is used).
     *
     * @param parentShell     the parent shell
     * @param dialogTitle     the dialog title
     * @param titleBarImage   the dialog shell/title-bar image, or {@code null}
     * @param message         the dialog message
     * @param dialogImageType one of the {@link org.eclipse.jface.dialogs.MessageDialog} image type constants
     * @param yesButtonLabel  the text for the affirmative button
     * @param noButtonLabel   the text for the negative button
     * @param defaultIndex    the index of the default button (0 for the yes button, 1 for the no button)
     * @param toggleMessage   the message for the toggle control, or {@code null} for the default message
     * @param toggleState     the initial state of the toggle
     */
    public QuestionWithToggleDialog(Shell parentShell, String dialogTitle, Image titleBarImage, String message,
            int dialogImageType, String yesButtonLabel, String noButtonLabel, int defaultIndex,
            String toggleMessage, boolean toggleState) {
        this(parentShell, dialogTitle, titleBarImage, message, dialogImageType, yesButtonLabel, noButtonLabel,
                defaultIndex, toggleMessage, toggleState, null);
    }

    /**
     * Convenience constructor that uses the translated, default Yes/No button
     * labels supplied by {@link QuestionWithToggleDialogMessages}.
     *
     * @param parentShell     the parent shell
     * @param dialogTitle     the dialog title
     * @param titleBarImage   the dialog shell/title-bar image, or {@code null}
     * @param message         the dialog message
     * @param dialogImageType one of the {@link org.eclipse.jface.dialogs.MessageDialog} image type constants
     * @param defaultIndex    the index of the default button (0 for the yes button, 1 for the no button)
     * @param toggleMessage   the message for the toggle control, or {@code null} for the default message
     * @param toggleState     the initial state of the toggle
     * @param messageIcon     a custom icon shown next to the message, or {@code null} to use the
     *                        standard system question icon
     */
    public QuestionWithToggleDialog(Shell parentShell, String dialogTitle, Image titleBarImage, String message,
            int dialogImageType, int defaultIndex, String toggleMessage, boolean toggleState, Image messageIcon) {
        this(parentShell, dialogTitle, titleBarImage, message, dialogImageType,
                QuestionWithToggleDialogMessages.questionDialogYesButtonLabel,
                QuestionWithToggleDialogMessages.questionDialogNoButtonLabel, defaultIndex, toggleMessage,
                toggleState, messageIcon);
    }

    /**
     * Same as the translated-labels convenience constructor, without a custom
     * message icon (the standard system question icon is used).
     *
     * @param parentShell     the parent shell
     * @param dialogTitle     the dialog title
     * @param titleBarImage   the dialog shell/title-bar image, or {@code null}
     * @param message         the dialog message
     * @param dialogImageType one of the {@link org.eclipse.jface.dialogs.MessageDialog} image type constants
     * @param defaultIndex    the index of the default button (0 for the yes button, 1 for the no button)
     * @param toggleMessage   the message for the toggle control, or {@code null} for the default message
     * @param toggleState     the initial state of the toggle
     */
    public QuestionWithToggleDialog(Shell parentShell, String dialogTitle, Image titleBarImage, String message,
            int dialogImageType, int defaultIndex, String toggleMessage, boolean toggleState) {
        this(parentShell, dialogTitle, titleBarImage, message, dialogImageType, defaultIndex, toggleMessage,
                toggleState, null);
    }

    /**
     * Builds the label-to-id map expected by {@code MessageDialogWithToggle}'s
     * {@link LinkedHashMap} based constructor, explicitly binding the Yes
     * button to {@link IDialogConstants#YES_ID} and the No button to
     * {@link IDialogConstants#NO_ID}, regardless of their translated label
     * text or declaration order.
     *
     * @param yesButtonLabel the text for the affirmative button
     * @param noButtonLabel  the text for the negative button
     * @return the ordered label-to-id map
     */
    private static LinkedHashMap<String, Integer> buttonLabelToIdMap(String yesButtonLabel, String noButtonLabel) {
        LinkedHashMap<String, Integer> buttonLabelToIdMap = new LinkedHashMap<>();
        buttonLabelToIdMap.put(yesButtonLabel, IDialogConstants.YES_ID);
        buttonLabelToIdMap.put(noButtonLabel, IDialogConstants.NO_ID);
        return buttonLabelToIdMap;
    }

    @Override
    public Image getImage() {
        if (messageIcon != null) {
            return messageIcon;
        }
        Image image = super.getImage();
        return image != null ? image : getQuestionImage();
    }
}

