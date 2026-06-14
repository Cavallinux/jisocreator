package cl.cavallinux.jisocreator.action.isoexplorer;

import java.io.File;
import java.util.EventObject;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;

import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.instances.IsoExplorerActionsManager;
import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenIsoEntryAction extends Action implements IDoubleClickListener, ISelectionChangedListener {
    private ITreeNode node;

    public OpenIsoEntryAction() {
        super("Open", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("run.png"));
        setToolTipText("Open a file or folder, in the layout");
        setEnabled(false);
    }

    @Override
    public void run() {
        File element = (File) node.getElement();
        if (element.isFile()) {
            OSAndIsoExplorerManager.INSTANCE.getOsExplorer().launch(element.toPath());
        } else {
            IsoExplorerSashForm isoSashFormInstance = GUIManager.INSTANCE.getMainWindow().getIsoExplorer();
            isoSashFormInstance.refresh(node);
        }
    }

    @Override
    public void doubleClick(DoubleClickEvent arg0) {
        setNode(arg0);
        if (arg0.getSource() instanceof TreeViewer) {
            if (GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree().getExpandedState(node)) {
                GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree().collapseToLevel(node, 1);
            } else {
                GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree().expandToLevel(node, 1);
            }
        } else {
            run();
        }
    }

    @Override
    public void selectionChanged(SelectionChangedEvent arg0) {
        setNode(arg0);
        if (arg0.getSource() instanceof TreeViewer) {
            if (node == null) {
                IsoExplorerActionsManager.DELETEISOENTRY.getAction().setEnabled(false);
                log.warn("SWT Library bug");
                return;
            } else {
                IsoExplorerActionsManager.DELETEISOENTRY.getAction().setEnabled(true);
            }
            /*
             * if (node.isRoot()) {
             * GoToIsoEntryParentAction.getInstance().setEnabled(false); } else {
             * GoToIsoEntryParentAction.getInstance().setEnabled(true); }
             */
            IsoExplorerActionsManager.GOTOISOPARENT.getAction().setEnabled(!node.isRoot());
            GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoTableText().setText(node.getIsoName());
            GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTable().setInput(node);
            setEnabled(false);
            IsoExplorerActionsManager.DELETEISOENTRY.getAction().setEnabled(false);

        } else {
            setEnabled(true);
            IsoExplorerActionsManager.DELETEISOENTRY.getAction().setEnabled(true);
        }
    }

    public void setNode(ITreeNode node) {
        this.node = node;
    }

    public void setNode(EventObject event) {
        IStructuredSelection selection;

        if (event instanceof DoubleClickEvent) {
            DoubleClickEvent dbe = (DoubleClickEvent) event;
            selection = (IStructuredSelection) dbe.getSelection();
        } else {
            SelectionChangedEvent sce = (SelectionChangedEvent) event;
            selection = (IStructuredSelection) sce.getSelection();
        }
        setNode((ITreeNode) selection.getFirstElement());
    }
}