package cl.cavallinux.jisocreator.action.main;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;

import cl.cavallinux.jisocreator.gui.i18n.MainActionsMessages;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NewIsoLayoutAction extends Action {
    public NewIsoLayoutAction() {
        super(MainActionsMessages.newIsoLayoutActionName,
                ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("new.png"));
        setToolTipText(MainActionsMessages.newIsoLayoutActionTooltip);
    }

    @Override
    public void run() {
        log.info("Loading new iso file system");
        IsoFileSystem iso = new IsoFileSystem();
        GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree().setInput(iso);
        GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree()
                .setSelection(new StructuredSelection(iso.getRoot()), true);
        GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree().expandToLevel(iso.getRoot(), 1);
    }
}