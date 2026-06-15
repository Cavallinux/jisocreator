package cl.cavallinux.jisocreator.gui.listeners.isoexplorer;

import java.util.Objects;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;

import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.IsoExplorerActionsManager;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ISOExplorerSashFormSelectionChangedListener implements ISelectionChangedListener {

 
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        ITreeNode node = obtainSelectionFromEvent(event);
        if (event.getSource() instanceof TreeViewer) {
            updateActionsAndIsoSashForm(node);
        } else {
            updateOpenAndDeleteISOActions();
        }
    }

    private void updateActionsAndIsoSashForm(ITreeNode node) {
        if (Objects.nonNull(node)) {
            /*
             * if (node.isRoot()) {
             * GoToIsoEntryParentAction.getInstance().setEnabled(false); } else {
             * GoToIsoEntryParentAction.getInstance().setEnabled(true); }
             */
            IsoExplorerActionsManager.GOTOISOPARENT.getAction().setEnabled(!node.isRoot());
            GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoTableText().setText(node.getIsoName());
            GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTable().setInput(node);
            IsoExplorerActionsManager.OPENISOENTRY.getAction().setEnabled(false);
            IsoExplorerActionsManager.DELETEISOENTRY.getAction().setEnabled(false);
        } else {
            IsoExplorerActionsManager.DELETEISOENTRY.getAction().setEnabled(false);
            log.warn("SWT Library bug");
        }
    }

    private void updateOpenAndDeleteISOActions() {
        IsoExplorerActionsManager.OPENISOENTRY.getAction().setEnabled(true);
        IsoExplorerActionsManager.DELETEISOENTRY.getAction().setEnabled(true);
    }
    
    private ITreeNode obtainSelectionFromEvent(SelectionChangedEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        return (ITreeNode) selection.getFirstElement();
    }
}
