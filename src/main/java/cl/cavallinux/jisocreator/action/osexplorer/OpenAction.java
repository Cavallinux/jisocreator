package cl.cavallinux.jisocreator.action.osexplorer;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class OpenAction extends JISOCreatorBaseAction {
    private File file;

    @Builder
    private OpenAction(String message, String tooltip, ImageDescriptor imageDescriptor) {
        super(message, tooltip, imageDescriptor);
        setEnabled(false);
        /*
                ;
        ;*/
    }

    @Override
    public void run() {
        if (file.isFile()) {
            log.info("Launching file: {}", file);
            OSAndIsoExplorerManager.INSTANCE.getOsExplorer().launch(file.toPath());
        } else {
            log.info("Setting file {} in tree viewer and triggering selection changed event", file);
            GUIManager.INSTANCE.getMainWindow().getOsExplorer().getOsDirectoriesTree()
                    .setSelection(GUIManager.INSTANCE.getMainWindow().getOsExplorer().getTableSelection());
            GUIManager.INSTANCE.getMainWindow().getOsExplorer().getOsDirectoriesTree().expandToLevel(file, 1);
            GUIManager.INSTANCE.getMainWindow().getOsExplorer().refresh();
        }
    }
}