package cl.cavallinux.jisocreator.gui.listeners;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;

import cl.cavallinux.jisocreator.action.osexplorer.AddFileAction;
import cl.cavallinux.jisocreator.action.osexplorer.OpenAction;
import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;

public class OSDirectoriesMenuListener implements IMenuListener {
    @Override
    public void menuAboutToShow(IMenuManager manager) {
        IStructuredSelection selection = OSExplorerSashForm.getInstance().getOsDirectoriesTable()
                .getStructuredSelection();
        if (!selection.isEmpty()) {
            manager.add(AddFileAction.getInstance());
            manager.add(OpenAction.getInstance());
        }
    }
}