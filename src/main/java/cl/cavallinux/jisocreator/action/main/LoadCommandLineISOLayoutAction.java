package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoadCommandLineISOLayoutAction extends Action {
    public void run(String layoutFilePath) {
        log.info("Loading iso layout from file: {}", layoutFilePath);
        IsoFileSystem iso = (IsoFileSystem) IOManager.INSTANCE.getIsoFilesystemParser().deserialize(layoutFilePath)
                .get();
        TreeViewer isoDirectoriesTree = GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree();
        isoDirectoriesTree.setInput(iso);
        isoDirectoriesTree.setSelection(new StructuredSelection(iso.getRoot()), true);
        isoDirectoriesTree.expandToLevel(iso.getRoot(), 1);
    }
}
