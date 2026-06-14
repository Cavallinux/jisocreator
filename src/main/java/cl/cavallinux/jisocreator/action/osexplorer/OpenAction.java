package cl.cavallinux.jisocreator.action.osexplorer;

import java.io.File;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class OpenAction extends Action {
    private File file;

    public OpenAction() {
        super("Open", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("run.png"));
        setToolTipText("Open a file or folder");
        setEnabled(false);
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