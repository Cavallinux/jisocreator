package cl.cavallinux.jisocreator.action.isoexplorer;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;

import cl.cavallinux.jisocreator.gui.i18n.IsoExplorerMessages;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeleteIsoEntryAction extends Action implements IRunnableWithProgress {
    private ITreeNode parent, node;

    @Builder
    private DeleteIsoEntryAction() {
        super(IsoExplorerMessages.isoExplorerDeleteEntryActionName,
                ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("delete.png"));
        setToolTipText(IsoExplorerMessages.isoExplorerDeleteEntryActionTooltip);
        setEnabled(false);
    }

    @Override
    public void run() {
        IStructuredSelection selection = (IStructuredSelection) GUIManager.INSTANCE.getMainWindow().getIsoExplorer()
                .getIsoDirectoriesTable().getSelection();
        node = (ITreeNode) selection.getFirstElement();
        parent = node.getParent();
        deleteNode();
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
            monitor.beginTask("Deleting node", IProgressMonitor.UNKNOWN);
            parent.deleteNode(node);
            monitor.subTask("Node deleted, refreshing GUI");
            Display.getDefault().asyncExec(() -> {
                GUIManager.INSTANCE.getMainWindow().getIsoExplorer().refresh();
            });
        } finally {
            monitor.done();
        }
    }

    private void deleteNode() {
        Display.getDefault().asyncExec(() -> {
            try {
                IProgressMonitor progressMonitor = GUIManager.INSTANCE.getMainWindow().getProgressMonitor();
                ModalContext.run(DeleteIsoEntryAction.this, true, progressMonitor, Display.getCurrent());
            } catch (InvocationTargetException | InterruptedException e) {
                log.error("Error loading ISO layout", e);
            }
        });
    }
}