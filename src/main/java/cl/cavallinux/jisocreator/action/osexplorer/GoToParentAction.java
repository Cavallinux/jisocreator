package cl.cavallinux.jisocreator.action.osexplorer;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;

public class GoToParentAction extends Action {
    public GoToParentAction() {
        super("Go to Parent File", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("up.png"));
        setToolTipText("Go to parent file");
        setEnabled(false);
    }

    @Override
    public void run() {
        IStructuredSelection selection = (IStructuredSelection) OSExplorerSashForm.getInstance().getTreeSelection();
        File file = (File) selection.getFirstElement();
        File parent = file.getParentFile();
        selection = new StructuredSelection(parent);
        OSExplorerSashForm.getInstance().getOsDirectoriesTree().setSelection(selection);
        setEnabled(OSAndIsoExplorerManager.INSTANCE.getOsExplorer().isRoot(file.toPath()));
    }
}