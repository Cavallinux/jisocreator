package cl.cavallinux.jisocreator.action.osexplorer;

import org.eclipse.jface.resource.ImageDescriptor;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;
import cl.cavallinux.jisocreator.instances.GUIManager;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RefreshExplorerAction extends JISOCreatorBaseAction {
    @Builder
    private RefreshExplorerAction(String message, String tooltip, ImageDescriptor imageDescriptor) {
        super(message, tooltip, imageDescriptor);
    }

    @Override
    public void run() {
        log.info("Refreshing OS Explorer");
        GUIManager.INSTANCE.getMainWindow().getOsExplorer().refresh();
    }
}