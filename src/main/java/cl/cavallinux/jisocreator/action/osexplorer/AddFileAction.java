package cl.cavallinux.jisocreator.action.osexplorer;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import cl.cavallinux.jisocreator.gui.dialog.ADDFileToIsoLayoutDialog;
import cl.cavallinux.jisocreator.gui.i18n.MainWindowMessages;
import cl.cavallinux.jisocreator.gui.i18n.OSExplorerMessages;
import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;
import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.instances.JFaceResourcesManager;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoTreeNode;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddFileAction extends Action implements IRunnableWithProgress {
    private ITreeNode isoNode;
    private List<File> files;

    @Builder
    private AddFileAction() {
        super(OSExplorerMessages.osExplorerAddActionName);
        setImageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("add.png"));
        setToolTipText("Add selected files to ISO9660 layout");
    }

    @Override
    public void run() {
        JFaceResourcesManager instance = JFaceResourcesManager.ISOEXPLORER_INSTANCE;
        MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
        Shell shell = mainWindow.getShell();
        ADDFileToIsoLayoutDialog isoTreeSelectionDialog = ADDFileToIsoLayoutDialog.builder().parent(shell)
                .contentProvider(instance.getTreeContentProvider()).labelProvider(instance.getTreeLabelProvider())
                .build();
        isoTreeSelectionDialog.setInput(mainWindow.getIsoExplorer().getIsoDirectoriesTree().getInput());

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
            isoNode.addNode(dirEntry);
        });
        monitor.subTask("Refreshing GUI...");
        Display.getDefault().asyncExec(new Thread(() -> {
            IsoExplorerSashForm isoExplorer = GUIManager.INSTANCE.getMainWindow().getIsoExplorer();
            isoExplorer.getIsoDirectoriesTree().setSelection(new StructuredSelection(isoNode), true);
            isoExplorer.getIsoDirectoriesTree().expandToLevel(isoNode, 1);
            isoExplorer.refresh();
            GUIManager.INSTANCE.getMainWindow()
                    .setStatus(isoExplorer.printISOFileSystemInfo(MainWindowMessages.isoFileSystemInfoStatusMessage));
        }));
        monitor.done();
    }

    @SuppressWarnings("unchecked")
    private void executeAddFiles(ITreeNode node) {
        OSExplorerSashForm osExplorer = GUIManager.INSTANCE.getMainWindow().getOsExplorer();
        IStructuredSelection selection = (IStructuredSelection) osExplorer.getTableSelection();
        files = selection.toList();
        isoNode = node;
    }

    private void executeAction() {
        try {
            IProgressMonitor progressMonitor = GUIManager.INSTANCE.getMainWindow().getProgressMonitor();
            ModalContext.run(this, true, progressMonitor, Display.getCurrent());
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