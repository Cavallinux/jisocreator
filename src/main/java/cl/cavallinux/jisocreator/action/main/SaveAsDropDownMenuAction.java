package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import cl.cavallinux.jisocreator.action.decl.ActionMenuCreator;
import cl.cavallinux.jisocreator.instances.ActionsManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;

public class SaveAsDropDownMenuAction extends ActionMenuCreator {
    private static SaveAsDropDownMenuAction instance;
    
    static {
        instance = new SaveAsDropDownMenuAction();
    }

    private SaveAsDropDownMenuAction() {
        super("Save as...");
        setImageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("saveas.png"));
        setMenuCreator(this);
    }

    @Override
    public void run() {
        ActionsManager.SAVEASISOACTION.getAction().run();
    }

    @Override
    public Menu getMenu(Control parent) {
        MenuManager menu = new MenuManager();
        menu.add(ActionsManager.SAVEASXMLACTION.getAction());
        menu.add(ActionsManager.SAVEASISOACTION.getAction());
        return menu.createContextMenu(parent);
    }

    public static SaveAsDropDownMenuAction getInstance() {
        return instance;
    }
}