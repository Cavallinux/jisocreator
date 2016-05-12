package cl.cavallinux.jisocreator.action.isoexplorer;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;

import cl.cavallinux.jisocreator.gui.dialog.BaseProgressMonitorDialog;
import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.util.ImageUtils;

public class DeleteIsoEntryAction extends Action implements IRunnableWithProgress {
    private static DeleteIsoEntryAction instance;
    private ITreeNode parent, node;

    private DeleteIsoEntryAction() {
	super("Delete", ImageUtils.getInstance().loadImageDescriptor("delete.png"));
	setToolTipText("Delete iso entry, from iso layout");
	setEnabled(false);
    }

    @Override
    public void run() {
	try {
	    IStructuredSelection selection = (IStructuredSelection) IsoExplorerSashForm.getInstance()
		    .getIsoDirectoriesTable().getSelection();
	    node = (ITreeNode) selection.getFirstElement();
	    parent = node.getParent();
	    ProgressMonitorDialog dialog = new BaseProgressMonitorDialog(MainWindow.getInstance().getShell());
	    dialog.run(true, false, this);
	    IsoExplorerSashForm.getInstance().getIsoDirectoriesTable().refresh();
	    IsoExplorerSashForm.getInstance().getIsoDirectoriesTree().refresh();
	    dialog.close();
	} catch (InvocationTargetException e) {
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
	try {
	    monitor.beginTask("Deleting node", IProgressMonitor.UNKNOWN);
	    parent.deleteNode(node, monitor);
	} finally {
	    monitor.done();
	}
    }

    public static DeleteIsoEntryAction getInstance() {
	if (instance == null) {
	    instance = new DeleteIsoEntryAction();
	}
	return instance;
    }
}