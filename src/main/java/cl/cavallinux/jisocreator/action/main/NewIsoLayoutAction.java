package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;

public class NewIsoLayoutAction extends Action {
    public NewIsoLayoutAction() {
        super("New Layout", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("new.png"));
        setToolTipText("Create new iso layout");
    }

    @Override
    public void run() {
        IsoFileSystem iso = new IsoFileSystem();
        GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree().setInput(iso);
        GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTable().setInput(iso.getRoot());
    }
}