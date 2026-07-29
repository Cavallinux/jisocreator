package cl.cavallinux.jisocreator.gui.dialog;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

/**
 * {@link MessageDialog} specialization that allows an optional custom icon to
 * be shown next to the error message, instead of always using the standard
 * system error icon.
 * <p>
 * When {@code messageIcon} is {@code null}, {@link #getImage()} falls back to
 * {@link #getErrorImage()}, preserving the original behaviour of a plain
 * {@code MessageDialog} built with {@link MessageDialog#ERROR}.
 * </p>
 */
public class ErrorMessageDialog extends MessageDialog {

    private final Image messageIcon;

    /**
     * Creates an error dialog, allowing a custom message icon to be supplied.
     *
     * @param parentShell   the parent shell
     * @param dialogTitle   the dialog title
     * @param titleBarImage the dialog shell/title-bar image, or {@code null}
     * @param message       the dialog message
     * @param dialogButtonLabels the labels for the dialog buttons
     * @param defaultIndex  the index of the default button
     * @param messageIcon   a custom icon shown next to the message, or {@code null} to use the
     *                      standard system error icon
     */
    public ErrorMessageDialog(Shell parentShell, String dialogTitle, Image titleBarImage, String message,
            String[] dialogButtonLabels, int defaultIndex, Image messageIcon) {
        super(parentShell, dialogTitle, titleBarImage, message, MessageDialog.ERROR, dialogButtonLabels,
                defaultIndex);
        this.messageIcon = messageIcon;
    }

    @Override
    public Image getImage() {
        if (messageIcon != null) {
            return messageIcon;
        }
        return getErrorImage();
    }
}
