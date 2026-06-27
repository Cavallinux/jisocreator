package cl.cavallinux.jisocreator.action.isoexplorer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;

import cl.cavallinux.jisocreator.gui.i18n.IsoExplorerMessages;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import lombok.Builder;

public class GoToIsoEntryParentAction extends Action {
    @Builder
    private GoToIsoEntryParentAction() {
        super(IsoExplorerMessages.isoExplorerGoToIsoParentActionName,
                ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("up.png"));
        setToolTipText(IsoExplorerMessages.isoExplorerGoToIsoParentActionTooltip);
        setEnabled(false);
    }

    @Override
    public void run() {
        StructuredSelection selection = (StructuredSelection) GUIManager.INSTANCE.getMainWindow().getIsoExplorer()
                .getIsoDirectoriesTree().getSelection();
        ITreeNode node = (ITreeNode) selection.getFirstElement();

        ITreeNode parent = node.getParent();
        selection = new StructuredSelection(parent);
        GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree().setSelection(selection);
    }
}