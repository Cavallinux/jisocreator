package cl.cavallinux.jisocreator.action.main;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.util.IOUtils;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * Action para cerrar la aplicacion.
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.0.2
 * @since 0.0.2
 */
@Slf4j
public class ExitApplicationAction extends JISOCreatorBaseAction {
    @Builder
    protected ExitApplicationAction(String message, String tooltip, ImageDescriptor imageDescriptor) {
        super(message, tooltip, imageDescriptor);
    }

    @Override
    public void run() {
        log.info("Confirming exit application");
        IOUtils ioUtils = IOManager.INSTANCE.getIoUtils();
        boolean openExitDialogConfirmation = ioUtils.getStore().getBoolean("general.exit.confirm");
        if (openExitDialogConfirmation) {
            openConfirmExitAppDialog();
        } else {
            exit();
        }
    }

    private void openConfirmExitAppDialog() {
        MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
        Shell shell = mainWindow.getShell();
        IOUtils ioUtils = IOManager.INSTANCE.getIoUtils();
        MessageDialogWithToggle dialog = MessageDialogWithToggle.openYesNoQuestion(shell, "JISOCreator",
                "Are you sure to exit?", "Ask always",
                ioUtils.getStore().getBoolean("general.exit.confirm"), null, null);

        ioUtils.getStore().setValue("general.exit.confirm", dialog.getToggleState());
        ioUtils.saveStore();
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
    
    public void halt() {
        System.exit(0);
    }
}