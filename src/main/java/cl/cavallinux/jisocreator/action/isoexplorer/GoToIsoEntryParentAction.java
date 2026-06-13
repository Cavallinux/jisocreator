package cl.cavallinux.jisocreator.action.isoexplorer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;

import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;

public class GoToIsoEntryParentAction extends Action {
    private static GoToIsoEntryParentAction instance;
    
    static {
        instance = new GoToIsoEntryParentAction();
    }

    private GoToIsoEntryParentAction() {
        super("Go to parent file", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("up.png"));
        setToolTipText("Go to parent file");
        setEnabled(false);
    }

    @Override
    public void run() {
        StructuredSelection selection = (StructuredSelection) IsoExplorerSashForm.getInstance().getIsoDirectoriesTree()
                .getSelection();
        ITreeNode node = (ITreeNode) selection.getFirstElement();

        ITreeNode parent = node.getParent();
        selection = new StructuredSelection(parent);
        IsoExplorerSashForm.getInstance().getIsoDirectoriesTree().setSelection(selection);
    }

    public static GoToIsoEntryParentAction getInstance() {
        return instance;
    }
}