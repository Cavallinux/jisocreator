package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;

import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoadCommandLineISOLayoutAction extends Action {
    public void run(String layoutFilePath) {
        log.info("Loading iso layout from file: {}", layoutFilePath);
        IsoFileSystem iso = (IsoFileSystem) IOManager.INSTANCE.getIoUtils().parseXMLFileToObject(layoutFilePath);
        GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree().setInput(iso);
        //
        GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree()
                .setSelection(new StructuredSelection(iso.getRoot()), true);
        GUIManager .INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree().expandToLevel(iso.getRoot(), 1);
    }
}
