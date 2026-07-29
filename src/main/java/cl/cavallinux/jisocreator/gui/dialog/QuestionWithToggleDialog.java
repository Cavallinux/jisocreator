package cl.cavallinux.jisocreator.gui.dialog;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

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
 */
public class QuestionWithToggleDialog extends MessageDialogWithToggle {

    public QuestionWithToggleDialog(Shell parentShell, String dialogTitle, Image image, String message,
            int dialogImageType, String[] dialogButtonLabels, int defaultIndex, String toggleMessage,
            boolean toggleState) {
        super(parentShell, dialogTitle, image, message, dialogImageType, dialogButtonLabels, defaultIndex,
                toggleMessage, toggleState);
    }

    @Override
    public Image getImage() {
        Image image = super.getImage();
        return image != null ? image : getQuestionImage();
    }
}
