package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import cl.cavallinux.jisocreator.gui.i18n.MainActionsMessages;
import cl.cavallinux.jisocreator.gui.i18n.MainWindowMessages;
import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class NewIsoLayoutAction extends Action {
    protected NewIsoLayoutAction() {
        super(MainActionsMessages.newIsoLayoutActionName);
        setImageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("new.png"));
        setToolTipText(MainActionsMessages.newIsoLayoutActionTooltip);
    }

    @Override
    public void run() {
        log.info("Loading new iso file system");
        IsoFileSystem iso = IsoFileSystem.builder().build();
        MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
        IsoExplorerSashForm isoExplorer = mainWindow.getIsoExplorer();
        TreeViewer isoDirectoriesTree = isoExplorer.getIsoDirectoriesTree();
        IStructuredSelection structuredSelecton = new StructuredSelection(iso.getRoot());
        isoDirectoriesTree.setInput(iso);
        isoDirectoriesTree.setSelection(structuredSelecton, true);
        isoDirectoriesTree.expandToLevel(iso.getRoot(), 1);
        mainWindow.setStatus(isoExplorer.printISOFileSystemInfo(MainWindowMessages.isoFileSystemInfoStatusMessage));
    }
}