package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import cl.cavallinux.jisocreator.action.decl.ActionMenuCreator;
import cl.cavallinux.jisocreator.util.ImageUtils;

public class SaveAsDropDownMenuAction extends ActionMenuCreator {
    private static SaveAsDropDownMenuAction instance;

    public SaveAsDropDownMenuAction() {
	super("Save as...");
	setImageDescriptor(ImageUtils.getInstance().loadImageDescriptor("saveas.png"));
	setMenuCreator(this);
    }

    @Override
    public void run() {
	SaveAsIsoAction.getInstance().run();
    }

    @Override
    public Menu getMenu(Control parent) {
	MenuManager menu = new MenuManager();
	menu.add(SaveAsXMLAction.getInstance());
	menu.add(SaveAsIsoAction.getInstance());
	return menu.createContextMenu(parent);
    }

    public static SaveAsDropDownMenuAction getInstance() {
	if (instance == null) {
	    instance = new SaveAsDropDownMenuAction();
	}
	return instance;
    }
}