package cl.cavallinux.jisocreator.action.osexplorer;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

import cl.cavallinux.jisocreator.gui.i18n.OSExplorerMessages;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoToParentAction extends Action {
    @Builder
    private GoToParentAction() {
        super(OSExplorerMessages.osExplorerGoToParentActionName,
                ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("up.png"));
        setToolTipText(OSExplorerMessages.osExplorerGoToParentActionToolTip);
        setEnabled(false);
    }

    @Override
    public void run() {
        log.info("Running go to parent action");
        IStructuredSelection selection = (IStructuredSelection) GUIManager.INSTANCE.getMainWindow().getOsExplorer()
                .getTreeSelection();
        File file = (File) selection.getFirstElement();
        File parent = file.getParentFile();
        selection = new StructuredSelection(parent);
        GUIManager.INSTANCE.getMainWindow().getOsExplorer().getOsDirectoriesTree().setSelection(selection);
        setEnabled(OSAndIsoExplorerManager.INSTANCE.getOsExplorer().isRoot(file.toPath()));
    }
}