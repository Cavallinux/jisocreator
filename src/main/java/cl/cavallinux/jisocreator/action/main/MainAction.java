package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.instances.ActionsManager;
import cl.cavallinux.jisocreator.instances.GUIManager;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase principal, contiene el metodo main para arrancar la aplicacion.
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.0.2
 * @since 0.0.2
 */
@Slf4j
@NoArgsConstructor
public class MainAction extends Action {
    @Override
    public void run() {
        log.info("Running application");
        GUIManager.INSTANCE.getMainWindow().setBlockOnOpen(true);
        GUIManager.INSTANCE.getMainWindow().open();
    }

    /**
     * Metodo principal por donde arranca la app
     * 
     * @param args Argumentos recibidos desde el sistema operativo.
     */
    public static void main(String[] args) {
        ActionsManager.MAINACTION.getAction().run();
    }
}