package cl.cavallinux.jisocreator.action.osexplorer;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.gui.i18n.OSExplorerMessages;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RefreshExplorerAction extends Action {
    @Builder
    private RefreshExplorerAction() {
        super(OSExplorerMessages.osExplorerRefreshActionName,
                ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("refresh.png"));
        setToolTipText(OSExplorerMessages.osExplorerRefreshActionToolTip);
    }

    @Override
    public void run() {
        log.info("Refreshing OS Explorer");
        GUIManager.INSTANCE.getMainWindow().getOsExplorer().refresh();
    }
}