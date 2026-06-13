package cl.cavallinux.jisocreator.action.osexplorer;

import java.io.File;
import java.util.EventObject;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;

import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import cl.cavallinux.jisocreator.instances.OSExplorerActionsManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class OpenAction extends Action implements IDoubleClickListener, ISelectionChangedListener {
    private File file;

    public OpenAction() {
        super("Open", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("run.png"));
        setToolTipText("Open a file or folder");
        setEnabled(false);
    }

    @Override
    public void run() {
        if (file.isFile()) {
            OSAndIsoExplorerManager.INSTANCE.getOsExplorer().launch(file.toPath());
        } else {
            OSExplorerSashForm.getInstance().getOsDirectoriesTree()
                    .setSelection(OSExplorerSashForm.getInstance().getOsDirectoriesTable().getSelection());
            OSExplorerSashForm.getInstance().getOsDirectoriesTree().expandToLevel(file, 1);
        }
    }

    @Override
    public void doubleClick(DoubleClickEvent arg0) {
        setFileSelected(arg0);
        if (arg0.getSource() instanceof TreeViewer) {
            if (OSExplorerSashForm.getInstance().getOsDirectoriesTree().getExpandedState(file)) {
                OSExplorerSashForm.getInstance().getOsDirectoriesTree().collapseToLevel(file, 1);
            } else {
                OSExplorerSashForm.getInstance().getOsDirectoriesTree().expandToLevel(file, 1);
            }
        } else {
            run();
        }
    }

    @Override
    public void selectionChanged(SelectionChangedEvent arg0) {
        setFileSelected(arg0);
        if (arg0.getSource() instanceof TreeViewer) {
            if (file == null) {
                OSExplorerActionsManager.ADDFILEACTION.getAction().setEnabled(false);
                OSExplorerActionsManager.GOTOPARENTACTION.getAction().setEnabled(false);
                OSExplorerSashForm.getInstance().getOsDirectoriesTable().setInput(new File(
                        ((File) OSExplorerSashForm.getInstance().getOsDirectoriesTable().getInput()).getParent()));
                OSExplorerSashForm.getInstance().getOsTableText().setText(new File(
                        ((File) OSExplorerSashForm.getInstance().getOsDirectoriesTable().getInput()).getParent())
                        .getAbsolutePath());
                log.warn("SWT Library bug");
                return;
            } else {
                OSExplorerActionsManager.ADDFILEACTION.getAction().setEnabled(true);
            }
            OSExplorerActionsManager.GOTOPARENTACTION.getAction()
                    .setEnabled(!OSAndIsoExplorerManager.INSTANCE.getOsExplorer().isRoot(file.toPath()));
            OSExplorerSashForm.getInstance().getOsTableText().setText(file.getAbsolutePath());
            OSExplorerSashForm.getInstance().getOsDirectoriesTable().setInput(file);
            setEnabled(false);
        } else {
            setEnabled(true);
        }
    }

    public void setFileSelected(EventObject event) {
        IStructuredSelection selection;

        if (event instanceof DoubleClickEvent) {
            DoubleClickEvent dbe = (DoubleClickEvent) event;
            selection = (IStructuredSelection) dbe.getSelection();
        } else {
            SelectionChangedEvent sce = (SelectionChangedEvent) event;
            selection = (IStructuredSelection) sce.getSelection();
        }
        this.setFile((File) selection.getFirstElement());
    }
}