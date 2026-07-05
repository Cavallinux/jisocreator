package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import cl.cavallinux.jisocreator.gui.dialog.AboutDialog;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.instances.GUIManager;
import lombok.Builder;

/**
 * Action que despliega el cuadro de dialog que muestra la version de la
 * aplicacion
 */
public class AboutAction extends Action {
    @Builder
    protected AboutAction(String message, String tooltip, ImageDescriptor imageDescriptor) {
        //message(MainActionsMessages.aboutActionName).tooltip(MainActionsMessages.aboutActionTooltip).imageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("about.png"));
        //(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("about.png"));
        //tooltip(MainActionsMessages.aboutActionTooltip);
        super(message);
        setImageDescriptor(imageDescriptor);
        setToolTipText(tooltip);
    }

    @Override
    public void run() {
        MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
        AboutDialog dialog = AboutDialog.builder().parentShell(mainWindow.getShell()).build();
        dialog.open();
        dialog.close();
    }
}