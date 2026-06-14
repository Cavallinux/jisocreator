package cl.cavallinux.jisocreator.action.osexplorer;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RefreshExplorerAction extends Action {
    public RefreshExplorerAction() {
        super("Refresh OS Explorer", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("refresh.png"));
        setToolTipText("Refresh OS Explorer");
    }

    @Override
    public void run() {
        log.info("Refreshing OS Explorer");
        OSExplorerSashForm.getInstance().refresh();
    }
}