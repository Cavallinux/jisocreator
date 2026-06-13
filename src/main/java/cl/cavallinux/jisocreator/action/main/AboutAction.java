package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.gui.dialog.AboutDialog;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;

/**
 * Action que despliega el cuadro de dialog que muestra la version de la aplicacion 
 */
public class AboutAction extends Action {
    
    /**
     * Constructor por defecto de la clase
     */
    public AboutAction() {
        super("About JIsoCreator", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("about.png"));
        setToolTipText("Open about dialog");
    }

    @Override
    public void run() {
        AboutDialog dialog = new AboutDialog(GUIManager.INSTANCE.getMainWindow().getShell());
        dialog.open();
        dialog.close();
    }
}