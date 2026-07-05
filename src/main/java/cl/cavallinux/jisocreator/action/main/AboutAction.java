package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.resource.ImageDescriptor;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;
import cl.cavallinux.jisocreator.gui.dialog.AboutDialog;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.instances.GUIManager;
import lombok.Builder;

/**
 * Action que despliega el cuadro de dialog que muestra la version de la
 * aplicacion
 */
public class AboutAction extends JISOCreatorBaseAction {
    @Builder
    protected AboutAction(String message, String tooltip, ImageDescriptor imageDescriptor) {
        super(message, tooltip, imageDescriptor);
    }

    @Override
    public void run() {
        MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
        AboutDialog dialog = AboutDialog.builder().parentShell(mainWindow.getShell()).build();
        dialog.open();
        dialog.close();
    }
}