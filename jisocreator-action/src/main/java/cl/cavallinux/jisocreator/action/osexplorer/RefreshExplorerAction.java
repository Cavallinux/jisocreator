package cl.cavallinux.jisocreator.action.osexplorer;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.util.ImageUtils;

public class RefreshExplorerAction extends Action {
    private static RefreshExplorerAction instance;

    private RefreshExplorerAction() {
	super("Refresh OS Explorer", ImageUtils.getInstance().loadImageDescriptor("refresh.png"));
	setToolTipText("Refresh OS Explorer");
    }

    @Override
    public void run() {
	OSExplorerSashForm.getInstance().getOsDirectoriesTable().refresh();
	OSExplorerSashForm.getInstance().getOsDirectoriesTree().refresh();
    }

    public static RefreshExplorerAction getInstance() {
	if (instance == null) {
	    instance = new RefreshExplorerAction();
	}
	return instance;
    }
}