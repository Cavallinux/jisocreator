package cl.cavallinux.jisocreator.action.osexplorer;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;
import cl.cavallinux.jisocreator.gui.dialog.ADDFileToIsoLayoutDialog;
import cl.cavallinux.jisocreator.gui.i18n.MainWindowMessages;
import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;
import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.JFaceResourcesManager;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoTreeNode;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddFileAction extends JISOCreatorBaseAction implements IRunnableWithProgress {
    private ITreeNode isoNode;
    private List<File> files;

    @Builder
    private AddFileAction(String message, String tooltip, ImageDescriptor imageDescriptor) {
        super(message, tooltip, imageDescriptor);
        files = null;
        isoNode = null;
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
    
    public void run(List<File> droppedFiles) {
        log.info("Running AddFileAction with {} dropped files from drag & drop", droppedFiles != null ? droppedFiles.size() : 0);
        
        if (droppedFiles == null || droppedFiles.isEmpty()) {
            log.warn("No files provided to AddFileAction");
            return;
        }

        this.files = droppedFiles;
        IsoExplorerSashForm isoExplorer = GUIManager.INSTANCE.getMainWindow().getIsoExplorer();
        IStructuredSelection isoSelection = (IStructuredSelection) isoExplorer.getIsoDirectoriesTable().getSelection();
        
        if (!isoSelection.isEmpty()) {
            this.isoNode = (ITreeNode) isoSelection.getFirstElement();
            log.info("Target ISO node selected from table: {}", isoNode);
        } else {
            IsoFileSystem isoFileSystem = (IsoFileSystem) isoExplorer.getIsoDirectoriesTree().getInput();
            if (isoFileSystem != null) {
                this.isoNode = isoFileSystem.getRoot();
                log.info("Target ISO node set to root: {}", isoNode);
            } else {
                log.error("No ISO file system or target node available");
                return;
            }
        }
        executeAction();
        
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
        monitor.beginTask("Adding selected files", IProgressMonitor.UNKNOWN);
        files.forEach(file -> {
            monitor.subTask(String.format("Adding file: %s", file.getAbsolutePath()));
            ITreeNode dirEntry = new IsoTreeNode(isoNode, file);
            isoNode.addNode(dirEntry);
        });
        monitor.subTask("Refreshing GUI...");
        Display.getDefault().asyncExec(new Thread(() -> {
            IsoExplorerSashForm isoExplorer = mainWindow.getIsoExplorer();
            TreeViewer isoDirectoriesTree = isoExplorer.getIsoDirectoriesTree();
            IStructuredSelection isoStructuredSelection = new StructuredSelection(isoNode);
            isoDirectoriesTree.setSelection(isoStructuredSelection, true);
            isoDirectoriesTree.expandToLevel(isoNode, 1);
            isoExplorer.refresh();
            String isoInfoStatus = isoExplorer.printISOFileSystemInfo(MainWindowMessages.isoFileSystemInfoStatusMessage);
            mainWindow.setStatus(isoInfoStatus);
        }));
        
        monitor.done();
    }

    @SuppressWarnings("unchecked")
    private void executeAddFiles(ITreeNode node) {
        MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
        OSExplorerSashForm osExplorer = mainWindow.getOsExplorer();
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
}