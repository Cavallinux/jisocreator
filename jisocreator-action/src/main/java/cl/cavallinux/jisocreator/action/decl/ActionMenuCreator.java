package cl.cavallinux.jisocreator.action.decl;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * Clase base para la creacion de los menus y asociarlos a los Action.
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @since 0.0.2
 * @version 0.0.2
 */
public abstract class ActionMenuCreator extends Action implements IMenuCreator {

    /**
     * Constructor de la clase
     * @param string Nombre del menu
     */
    public ActionMenuCreator(String string) {
        super(string, AS_DROP_DOWN_MENU);
    }

    @Override
    public void dispose() {
    }

    @Override
    public Menu getMenu(Control arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Menu getMenu(Menu arg0) {
        // TODO Auto-generated method stub
        return null;
    }
}