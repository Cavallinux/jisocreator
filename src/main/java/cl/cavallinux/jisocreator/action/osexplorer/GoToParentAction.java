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
        IStructuredSelection selection = (IStructuredSelection) osExplorer.getTreeSelection();
        File file = (File) selection.getFirstElement();
        File parent = file.getParentFile();
        selection = new StructuredSelection(parent);
        osExplorer.setTreeSelection(selection);
        setEnabled(OSAndIsoExplorerManager.INSTANCE.getOsExplorer().isRoot(file.toPath()));
    }
}