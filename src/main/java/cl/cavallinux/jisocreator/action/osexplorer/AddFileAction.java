package cl.cavallinux.jisocreator.action.osexplorer;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;
import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoTreeNode;
import cl.cavallinux.jisocreator.model.providers.impl.isoexplorer.IsoTreeContentProvider;
import cl.cavallinux.jisocreator.model.providers.impl.isoexplorer.IsoTreeLabelProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddFileAction extends Action implements IRunnableWithProgress {
    private ITreeNode isoNode;
    private List<File> files;
    
    public AddFileAction() {
        super("Add", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("add.png"));
        setToolTipText("Add selected files to ISO9660 layout");
    }

    @Override
    public void run() {
        ElementTreeSelectionDialog isoTreeSelectionDialog = new ElementTreeSelectionDialog(
                Display.getDefault().getActiveShell(), new IsoTreeLabelProvider(), new IsoTreeContentProvider());
        isoTreeSelectionDialog.setTitle("Iso tree");
        isoTreeSelectionDialog.setMessage("Select the destination directory to place the selected files.");
        isoTreeSelectionDialog.setInput(IsoExplorerSashForm.getInstance().getIsoDirectoriesTree().getInput());

        switch (isoTreeSelectionDialog.open()) {
        case Window.OK:
            executeAddFiles((ITreeNode) isoTreeSelectionDialog.getFirstResult());
            executeAction();
            isoTreeSelectionDialog.close();
        default:
            return;
        }
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        monitor.beginTask("Adding selected files", IProgressMonitor.UNKNOWN);
        monitor.subTask("Calculating quantity of files to add...");
        int i = calculateFileQuantity();
        monitor.done();
        monitor.beginTask("Adding selected files", i);
        files.forEach(file -> {
            ITreeNode dirEntry = new IsoTreeNode(isoNode, file);
            isoNode.addNode(dirEntry, monitor);
        });
        monitor.subTask("Refreshing GUI...");
        Display.getDefault().asyncExec(new Thread(() -> {
            IsoExplorerSashForm.getInstance().getIsoDirectoriesTree().refresh();
        }));
        monitor.done();
    }

    @SuppressWarnings("unchecked")
    private void executeAddFiles(ITreeNode node) {
        IStructuredSelection selection = (IStructuredSelection) OSExplorerSashForm.getInstance().getOsDirectoriesTable()
                .getSelection();
        files = selection.toList();
        isoNode = node;
    }

    private void executeAction() {
        try {
            ModalContext.run(this, true, GUIManager.INSTANCE.getMainWindow().getProgressMonitor(),
                    Display.getCurrent());
        } catch (InvocationTargetException | InterruptedException e) {
            log.error("Error executing AddFileAction", e);
            return;
        }
    }

    private int calculateFileQuantity() {
        int q = 0;
        for (File file : files) {
            q += calculateFileQuantity(file);
        }
        return q;
    }

    private int calculateFileQuantity(File file) {
        int i = 0;
        if (file.isFile() || (file.listFiles() == null)) {
            i++;
        } else {
            for (File temp : file.listFiles()) {
                i += calculateFileQuantity(temp);
            }
        }
        return i;
    }
}