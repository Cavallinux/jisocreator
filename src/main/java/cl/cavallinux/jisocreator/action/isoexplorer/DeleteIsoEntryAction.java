package cl.cavallinux.jisocreator.action.isoexplorer;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeleteIsoEntryAction extends JISOCreatorBaseAction implements IRunnableWithProgress {
    private ITreeNode parent, node;

    @Builder
    private DeleteIsoEntryAction(String message, String tooltip, ImageDescriptor imageDescriptor) {
        super(message, tooltip, imageDescriptor);
        setEnabled(false);
    }

    @Override
    public void run() {
        MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
        TableViewer isoDirectoriesTable = mainWindow.getIsoExplorer().getIsoDirectoriesTable();
        IStructuredSelection selection = (IStructuredSelection) isoDirectoriesTable.getSelection();
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
                MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
                mainWindow.getIsoExplorer().refresh();
            });
        } finally {
            monitor.done();
        }
    }

    private void deleteNode() {
        Display.getDefault().asyncExec(() -> {
            try {
                MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
                Display current = Display.getCurrent();
                IProgressMonitor progressMonitor = mainWindow.getProgressMonitor();
                ModalContext.run(DeleteIsoEntryAction.this, true, progressMonitor, current);
            } catch (InvocationTargetException | InterruptedException e) {
                log.error("Error loading ISO layout", e);
            }
        });
    }
}