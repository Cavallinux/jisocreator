package cl.cavallinux.jisocreator.action.osexplorer;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;

import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.util.ImageUtils;

public class GoToParentAction extends Action {
    private static GoToParentAction instance;

    static {
	instance = new GoToParentAction();
    }

    public GoToParentAction() {
	super("Go to Parent File", ImageUtils.getInstance().loadImageDescriptor("up.png"));
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
    }

    public static GoToParentAction getInstance() {
	return instance;
    }
}