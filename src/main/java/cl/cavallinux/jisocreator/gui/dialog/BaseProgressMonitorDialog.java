package cl.cavallinux.jisocreator.gui.dialog;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;

import cl.cavallinux.jisocreator.util.ImageUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseProgressMonitorDialog extends ProgressMonitorDialog {

    public BaseProgressMonitorDialog(Shell parent) {
        super(parent);
    }

    @Override
    protected void configureShell(Shell shell) {
        log.info("Configuring progress monitor dialog shell");
        super.configureShell(shell);
        shell.setText("Progress Information");
        shell.setImage(ImageUtils.getInstance().loadImage("iso.png"));
    }
}