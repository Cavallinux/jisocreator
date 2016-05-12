package cl.cavallinux.jisocreator.gui.dialog;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;

import cl.cavallinux.jisocreator.util.ImageUtils;

public class BaseProgressMonitorDialog extends ProgressMonitorDialog {

    public BaseProgressMonitorDialog(Shell parent) {
	super(parent);
    }

    @Override
    protected void configureShell(Shell shell) {
	super.configureShell(shell);
	shell.setText("Progress Information");
	shell.setImage(ImageUtils.getInstance().loadImage("iso.png"));
    }
}