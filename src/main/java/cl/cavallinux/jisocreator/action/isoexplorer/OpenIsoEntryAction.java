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
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenIsoEntryAction extends Action implements IDoubleClickListener, ISelectionChangedListener {
    private ITreeNode node;
    private static OpenIsoEntryAction instance;

    static {
        instance = new OpenIsoEntryAction();
    }

    private OpenIsoEntryAction() {
        super("Open", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("run.png"));
        setToolTipText("Open a file or folder, in the layout");
        setEnabled(false);
    }

    @Override
    public void run() {
        File element = (File) node.getElement();
        if (element.isFile()) {
            OSExplorer.getInstance().launch(element.toPath());
        } else {
            IsoExplorerSashForm isoSashFormInstance = IsoExplorerSashForm.getInstance();
            isoSashFormInstance.refresh(node);
        }
    }

    @Override
    public void doubleClick(DoubleClickEvent arg0) {
        setNode(arg0);
        if (arg0.getSource() instanceof TreeViewer) {
            if (IsoExplorerSashForm.getInstance().getIsoDirectoriesTree().getExpandedState(node)) {
                IsoExplorerSashForm.getInstance().getIsoDirectoriesTree().collapseToLevel(node, 1);
            } else {
                IsoExplorerSashForm.getInstance().getIsoDirectoriesTree().expandToLevel(node, 1);
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
                DeleteIsoEntryAction.getInstance().setEnabled(false);
                log.warn("SWT Library bug");
                return;
            } else {
                DeleteIsoEntryAction.getInstance().setEnabled(true);
            }
            /*if (node.isRoot()) {
                GoToIsoEntryParentAction.getInstance().setEnabled(false);
            } else {
                GoToIsoEntryParentAction.getInstance().setEnabled(true);
            }*/
            GoToIsoEntryParentAction.getInstance().setEnabled(!node.isRoot());
            IsoExplorerSashForm.getInstance().getIsoTableText().setText(node.getIsoName());
            IsoExplorerSashForm.getInstance().getIsoDirectoriesTable().setInput(node);
            setEnabled(false);
            DeleteIsoEntryAction.getInstance().setEnabled(false);

        } else {
            setEnabled(true);
            DeleteIsoEntryAction.getInstance().setEnabled(true);
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

    public static OpenIsoEntryAction getInstance() {
        return instance;
    }
}