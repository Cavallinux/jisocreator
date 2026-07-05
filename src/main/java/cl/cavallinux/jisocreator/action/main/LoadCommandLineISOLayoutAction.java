package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import cl.cavallinux.jisocreator.gui.i18n.MainWindowMessages;
import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.model.parser.IsoFilesystemParser;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class LoadCommandLineISOLayoutAction extends Action {
    
    public void run(String layoutFilePath) {
        log.info("Loading iso layout from file: {}", layoutFilePath);
        IsoFilesystemParser<IsoFileSystem> isoFilesystemParser = IOManager.INSTANCE.getIsoFilesystemParser();
        IsoFileSystem iso = isoFilesystemParser.deserialize(layoutFilePath).get();
        MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
        IsoExplorerSashForm isoExplorer = mainWindow.getIsoExplorer();
        TreeViewer isoDirectoriesTree = isoExplorer.getIsoDirectoriesTree();
        isoDirectoriesTree.setInput(iso);
        isoDirectoriesTree.setSelection(new StructuredSelection(iso.getRoot()), true);
        isoDirectoriesTree.expandToLevel(iso.getRoot(), 1);
        mainWindow.setStatus(isoExplorer.printISOFileSystemInfo(MainWindowMessages.isoFileSystemInfoStatusMessage));
    }
}
