package cl.cavallinux.jisocreator.action.osexplorer;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.util.ImageUtils;

public class RefreshExplorerAction extends Action {
    private static RefreshExplorerAction instance;
    
    static {
        instance = new RefreshExplorerAction();
    }

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
        return instance;
    }
}