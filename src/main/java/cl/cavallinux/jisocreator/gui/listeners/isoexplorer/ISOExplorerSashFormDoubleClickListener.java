package cl.cavallinux.jisocreator.gui.listeners.isoexplorer;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import cl.cavallinux.jisocreator.action.isoexplorer.OpenIsoEntryAction;
import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.IsoExplorerActionsManager;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ISOExplorerSashFormDoubleClickListener implements IDoubleClickListener {
    @Override
    public void doubleClick(DoubleClickEvent event) {
        ITreeNode node = obtainSelectionFromEvent(event);
        Object eventSource = event.getSource();
        if (eventSource instanceof TreeViewer) {
            updateISODirectoresTree(node);
        } else {
            shootOpenIsoEntryAction(node);
        }
    }

    private void shootOpenIsoEntryAction(ITreeNode node) {
        OpenIsoEntryAction action = (OpenIsoEntryAction) IsoExplorerActionsManager.OPENISOENTRY.getAction();
        action.setNode(node);
        action.run();
    }

    private void updateISODirectoresTree(ITreeNode node) {
        IsoExplorerSashForm isoExplorer = GUIManager.INSTANCE.getMainWindow().getIsoExplorer();
        if (isoExplorer.getIsoDirectoriesTree().getExpandedState(node)) {
            isoExplorer.getIsoDirectoriesTree().collapseToLevel(node, 1);
        } else {
            isoExplorer.getIsoDirectoriesTree().expandToLevel(node, 1);
        }
    }

    private ITreeNode obtainSelectionFromEvent(DoubleClickEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        return (ITreeNode) selection.getFirstElement();
    }
}
