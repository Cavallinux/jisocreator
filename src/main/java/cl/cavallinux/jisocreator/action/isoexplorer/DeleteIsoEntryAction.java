package cl.cavallinux.jisocreator.action.isoexplorer;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;

import cl.cavallinux.jisocreator.gui.dialog.BaseProgressMonitorDialog;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeleteIsoEntryAction extends Action implements IRunnableWithProgress {
    private ITreeNode parent, node;

    public DeleteIsoEntryAction() {
        super("Delete", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("delete.png"));
        setToolTipText("Delete iso entry, from iso layout");
        setEnabled(false);
    }

    @Override
    public void run() {
        try {
            IStructuredSelection selection = (IStructuredSelection) GUIManager.INSTANCE.getMainWindow().getIsoExplorer()
                    .getIsoDirectoriesTable().getSelection();
            node = (ITreeNode) selection.getFirstElement();
            parent = node.getParent();
            ProgressMonitorDialog dialog = new BaseProgressMonitorDialog(
                    GUIManager.INSTANCE.getMainWindow().getShell());
            dialog.run(true, false, this);
            GUIManager.INSTANCE.getMainWindow().getIsoExplorer().refresh();
            dialog.close();
        } catch (InvocationTargetException | InterruptedException e) {
            log.error("Error while processing delete action: ", e);
        }
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
            monitor.beginTask("Deleting node", IProgressMonitor.UNKNOWN);
            parent.deleteNode(node);
        } finally {
            monitor.done();
        }
    }
}