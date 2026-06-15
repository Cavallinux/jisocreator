package cl.cavallinux.jisocreator.action.osexplorer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Display;

import cl.cavallinux.jisocreator.action.jobs.ToggleHiddenFilesOSExplorerThread;
import cl.cavallinux.jisocreator.instances.ImageRegister;

public class ShowHiddenFilesAction extends Action {
    public ShowHiddenFilesAction() {
        super("Show hidden", IAction.AS_CHECK_BOX);
        setImageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("showhidden.svg"));
        setToolTipText("Show hidden files of the OS explorer");
    }

    @Override
    public void run() {
        Display.getCurrent().asyncExec(new ToggleHiddenFilesOSExplorerThread());
    }
}