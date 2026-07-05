package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.gui.dialog.AboutDialog;
import cl.cavallinux.jisocreator.gui.i18n.MainActionsMessages;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import lombok.Builder;

/**
 * Action que despliega el cuadro de dialog que muestra la version de la
 * aplicacion
 */
public class AboutAction extends Action {
    @Builder
    protected AboutAction() {
        super(MainActionsMessages.aboutActionName);
        setImageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("about.png"));
        setToolTipText(MainActionsMessages.aboutActionTooltip);
    }

    @Override
    public void run() {
        MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
        AboutDialog dialog = AboutDialog.builder().parentShell(mainWindow.getShell()).build();
        dialog.open();
        dialog.close();
    }
}