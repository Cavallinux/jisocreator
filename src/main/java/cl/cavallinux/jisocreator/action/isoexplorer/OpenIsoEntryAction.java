package cl.cavallinux.jisocreator.action.isoexplorer;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;
import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class OpenIsoEntryAction extends JISOCreatorBaseAction{
    private ITreeNode node;

    @Builder
    private OpenIsoEntryAction(String message, String tooltip, ImageDescriptor imageDescriptor) {
        super(message, tooltip, imageDescriptor);
        setEnabled(false);
    }

    @Override
    public void run() {
        File element = (File) node.getElement();
        if (element.isFile()) {
            OSExplorer osExplorer = OSAndIsoExplorerManager.INSTANCE.getOsExplorer();
            osExplorer.launch(element.toPath());
        } else {
            MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
            IsoExplorerSashForm isoSashFormInstance = mainWindow.getIsoExplorer();
            isoSashFormInstance.refresh(node);
        }
    }
}