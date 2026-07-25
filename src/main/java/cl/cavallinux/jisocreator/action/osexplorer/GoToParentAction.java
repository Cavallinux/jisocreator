package cl.cavallinux.jisocreator.action.osexplorer;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;
import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoToParentAction extends JISOCreatorBaseAction {
    @Builder
    private GoToParentAction(String message, String tooltip, ImageDescriptor imageDescriptor) {
        super(message, tooltip, imageDescriptor);
        setEnabled(false);
    }

    @Override
    public void run() {
        log.info("Running go to parent action");
        MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
        OSExplorerSashForm osExplorer = mainWindow.getOsExplorer();
        IStructuredSelection selection = osExplorer.getTreeSelection();
        File file = (File) selection.getFirstElement();
        File parent = file.getParentFile();
        selection = new StructuredSelection(parent);
        osExplorer.setTreeSelection(selection);
        OSExplorer osExplorerInstance = OSAndIsoExplorerManager.INSTANCE.getOsExplorer();
        setEnabled(!osExplorerInstance.isRoot(parent.toPath()));
    }
}