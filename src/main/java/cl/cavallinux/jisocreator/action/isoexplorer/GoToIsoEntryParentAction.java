package cl.cavallinux.jisocreator.action.isoexplorer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoToIsoEntryParentAction extends JISOCreatorBaseAction {
    @Builder
    private GoToIsoEntryParentAction(String message, String tooltip, ImageDescriptor imageDescriptor) {
        super(message, tooltip, imageDescriptor);
        setEnabled(false);
    }

    @Override
    public void run() {
        log.info("Go to ISO Entry Parent");
        MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
        TreeViewer isoDirectoriesTree = mainWindow.getIsoExplorer().getIsoDirectoriesTree();
        StructuredSelection selection = (StructuredSelection) isoDirectoriesTree.getSelection();
        ITreeNode node = (ITreeNode) selection.getFirstElement();
        ITreeNode parent = node.getParent();
        selection = new StructuredSelection(parent);
        isoDirectoriesTree.setSelection(selection);
    }
}