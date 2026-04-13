package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.util.ImageUtils;

public class ExitApplicationAction extends Action {
    private static ExitApplicationAction instance;

    private ExitApplicationAction() {
        super("Exit");
        setToolTipText("Create new iso layout");
        setImageDescriptor(ImageUtils.getInstance().loadImageDescriptor("exit.png"));
    }

    @Override
    public void run() {
        if (MessageDialog.openConfirm(MainWindow.getInstance().getShell(), "Confirm",
                "Are you sure to exit JIsocreator?")) {
            MainWindow.getInstance().getShell().setVisible(false);
            MainWindow.getInstance().close();
            Display.getCurrent().dispose();
            System.exit(0);
        } else {
            return;
        }
    }

    public static ExitApplicationAction getInstance() {
        if (instance == null) {
            instance = new ExitApplicationAction();
        }
        return instance;
    }
}