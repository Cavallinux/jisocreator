package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import lombok.extern.slf4j.Slf4j;

/**
 * Action para cerrar la aplicacion.
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.0.2
 * @since 0.0.2
 */
@Slf4j
public class ExitApplicationAction extends Action {
    public ExitApplicationAction() {
        super("Exit");
        setToolTipText("Create new iso layout");
        setImageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("exit.png"));
    }

    @Override
    public void run() {
        log.info("Confirming exit application");
        boolean openExitDialogConfirmation = IOManager.INSTANCE.getIoUtils().getStore()
                .getBoolean("general.exit.confirm");
        if (openExitDialogConfirmation) {
            openConfirmExitAppDialog();
        } else {
            exit();
        }
    }

    private void openConfirmExitAppDialog() {
        Shell shell = GUIManager.INSTANCE.getMainWindow().getShell();
        boolean confirmExit = MessageDialog.openConfirm(shell, "Confirm", "Are you sure to exit JIsocreator?");
        if (confirmExit) {
            exit();
        } else {
            return;
        }
    }
    
    private void exit() {
        log.info("Exiting application");
        Shell shell = GUIManager.INSTANCE.getMainWindow().getShell();
        shell.setVisible(false);
        GUIManager.INSTANCE.getMainWindow().close();
        Display.getCurrent().dispose();
        System.exit(0);
    }
}