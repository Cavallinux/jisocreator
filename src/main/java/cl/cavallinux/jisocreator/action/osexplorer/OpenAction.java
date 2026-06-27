package cl.cavallinux.jisocreator.action.osexplorer;

import java.io.File;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.gui.i18n.OSExplorerMessages;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class OpenAction extends Action {
    private File file;

    @Builder
    private OpenAction() {
        super(OSExplorerMessages.osExplorerOpenActionName,
                ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("run.png"));
        setToolTipText(OSExplorerMessages.osExplorerOpenActionToolTip);
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