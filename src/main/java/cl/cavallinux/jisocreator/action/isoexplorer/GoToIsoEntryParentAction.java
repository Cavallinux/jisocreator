package cl.cavallinux.jisocreator.action.isoexplorer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import cl.cavallinux.jisocreator.gui.i18n.IsoExplorerMessages;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.util.ImageUtils;
import lombok.Builder;

public class GoToIsoEntryParentAction extends Action {
    @Builder
    private GoToIsoEntryParentAction() {
        super(IsoExplorerMessages.isoExplorerGoToIsoParentActionName);
        ImageUtils imageUtils = ImageRegister.INSTANCE.getImageUtils();
        setImageDescriptor(imageUtils.loadImageDescriptor("up.png"));
        setToolTipText(IsoExplorerMessages.isoExplorerGoToIsoParentActionTooltip);
        setEnabled(false);
    }

    @Override
    public void run() {
        MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
        TreeViewer isoDirectoriesTree = mainWindow.getIsoExplorer().getIsoDirectoriesTree();
        StructuredSelection selection = (StructuredSelection) isoDirectoriesTree.getSelection();
        ITreeNode node = (ITreeNode) selection.getFirstElement();
        ITreeNode parent = node.getParent();
        selection = new StructuredSelection(parent);
        isoDirectoriesTree.setSelection(selection);
    }
}