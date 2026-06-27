package cl.cavallinux.jisocreator.action.osexplorer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Display;

import cl.cavallinux.jisocreator.action.jobs.ToggleHiddenFilesOSExplorerThread;
import cl.cavallinux.jisocreator.gui.i18n.OSExplorerMessages;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShowHiddenFilesAction extends Action {
    @Builder
    public ShowHiddenFilesAction() {
        super(OSExplorerMessages.osExplorerShowHiddenActionName, IAction.AS_CHECK_BOX);
        setImageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("showhidden.svg"));
        setToolTipText(OSExplorerMessages.osExplorerShowHiddenActionToolTip);
    }

    @Override
    public void run() {
        log.info("Showing of hiding hidden files");
        Display.getCurrent().asyncExec(ToggleHiddenFilesOSExplorerThread.builder().build());
    }
}