package cl.cavallinux.jisocreator.action.main;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.gui.window.MainWindow;

/**
 * Clase principal, contiene el metodo main para arrancar la aplicacion.
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.0.2
 * @since 0.0.2
 */
public class MainAction extends Action {
    private static MainAction instance = null;

    static {
        instance = new MainAction();
    }

    @Override
    public void run() {
        MainWindow.getInstance().setBlockOnOpen(true);
        MainWindow.getInstance().open();
    }

    /**
     * Metodo principal por donde arranca la app
     * 
     * @param args Argumentos recibidos desde el sistema operativo.
     */
    public static void main(String[] args) {
        PropertyConfigurator.configure(System.getProperty("log4j.file"));
        getInstance().run();
    }

    /**
     * Obtiene la instancia del objeto indicado.
     * 
     * @return un {@link MainAction}
     */
    public static MainAction getInstance() {
        return instance;
    }
}