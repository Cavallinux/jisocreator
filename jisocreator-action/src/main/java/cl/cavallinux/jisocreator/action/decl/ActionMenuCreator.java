package cl.cavallinux.jisocreator.action.decl;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

public abstract class ActionMenuCreator extends Action implements IMenuCreator {

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