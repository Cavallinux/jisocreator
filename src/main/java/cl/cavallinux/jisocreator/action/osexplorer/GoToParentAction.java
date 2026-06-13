package cl.cavallinux.jisocreator.action.osexplorer;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;

import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;

public class GoToParentAction extends Action {
    public GoToParentAction() {
        super("Go to Parent File", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("up.png"));
        setToolTipText("Go to parent file");
        setEnabled(false);
    }

    @Override
    public void run() {
        StructuredSelection selection = (StructuredSelection) OSExplorerSashForm.getInstance().getOsDirectoriesTree()
                .getSelection();
        File file = (File) selection.getFirstElement();

        File parent = file.getParentFile();
        selection = new StructuredSelection(parent);
        OSExplorerSashForm.getInstance().getOsDirectoriesTree().setSelection(selection);
        setEnabled(OSExplorer.getInstance().isRoot(file.toPath()));
    }
}