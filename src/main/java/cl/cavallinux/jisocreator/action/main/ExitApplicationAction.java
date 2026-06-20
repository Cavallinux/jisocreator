package cl.cavallinux.jisocreator.action.main;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Shell;

import cl.cavallinux.jisocreator.gui.window.MainWindow;
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
        MessageDialogWithToggle dialog = MessageDialogWithToggle.openYesNoQuestion(shell, "JISOCreator",
                "Are you sure to exit?", "Ask always",
                IOManager.INSTANCE.getIoUtils().getStore().getBoolean("general.exit.confirm"), null, null);

        IOManager.INSTANCE.getIoUtils().getStore().setValue("general.exit.confirm", dialog.getToggleState());
        IOManager.INSTANCE.getIoUtils().saveStore();
        switch (dialog.getReturnCode()) {
        case IDialogConstants.YES_ID:
            exit();
        default:
            return;
        }
    }

    private void exit() {
        log.info("Exiting application");
        cancelAliveIsoProcessIfNeeded();
        closeMainWindow();
        halt();
    }

    private void closeMainWindow() {
        log.info("Closing main window");
        MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
        mainWindow.setVisible(false);
        mainWindow.close();
        log.info("Main window closed");
    }

    private void cancelAliveIsoProcessIfNeeded() {
        log.info("Forcing killing iso saving process");
        IProgressMonitor mainWindowProgressMonitor = GUIManager.INSTANCE.getMainWindow().getProgressMonitor();
        mainWindowProgressMonitor.setCanceled(true);
    }
    
    private void halt() {
        System.exit(0);
    }
}