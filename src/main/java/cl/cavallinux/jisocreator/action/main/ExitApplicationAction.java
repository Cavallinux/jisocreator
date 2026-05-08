package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.util.ImageUtils;
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
    private static ExitApplicationAction instance;

    static {
        instance = new ExitApplicationAction();
    }

    /**
     * Constructor por defecto, con acceso privado para prevenir instanciacion desde
     * otras clases.
     */
    private ExitApplicationAction() {
        super("Exit");
        setToolTipText("Create new iso layout");
        setImageDescriptor(ImageUtils.getInstance().loadImageDescriptor("exit.png"));
    }

    @Override
    public void run() {
        log.info("Confirming exit application");
        MainWindow mainWindow = MainWindow.getInstance();
        Shell shell = mainWindow.getShell();
        boolean confirmExit = MessageDialog.openConfirm(shell, "Confirm", "Are you sure to exit JIsocreator?");
        if (confirmExit) {
            log.info("Exiting application");
            shell.setVisible(false);
            mainWindow.close();
            Display.getCurrent().dispose();
            System.exit(0);
        } else {
            return;
        }
    }

    /**
     * Obtiene la instalacia de la clase.
     * 
     * @return un {@link ExitApplicationAction}
     */
    public static ExitApplicationAction getInstance() {
        return instance;
    }
}