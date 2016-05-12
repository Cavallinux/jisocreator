package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.util.ImageUtils;

public class NewIsoLayoutAction extends Action {
    private static NewIsoLayoutAction instance;

    public NewIsoLayoutAction() {
	super("New Layout", ImageUtils.getInstance().loadImageDescriptor("new.png"));
	setToolTipText("Create new iso layout");
    }

    @Override
    public void run() {
	IsoFileSystem iso = new IsoFileSystem();
	IsoExplorerSashForm.getInstance().getIsoDirectoriesTree().setInput(iso);
	IsoExplorerSashForm.getInstance().getIsoDirectoriesTable().setInput(iso.getRoot());
    }

    public static NewIsoLayoutAction getInstance() {
	if (instance == null) {
	    instance = new NewIsoLayoutAction();
	}
	return instance;
    }
}