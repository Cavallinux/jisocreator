package cl.cavallinux.jisocreator.gui.listeners;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;

import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.instances.OSExplorerActionsManager;

public class OSDirectoriesMenuListener implements IMenuListener {
    @Override
    public void menuAboutToShow(IMenuManager manager) {
        IStructuredSelection selection = OSExplorerSashForm.getInstance().getOsDirectoriesTable()
                .getStructuredSelection();
        if (!selection.isEmpty()) {
            manager.add(OSExplorerActionsManager.ADDFILEACTION.getAction());
            manager.add(OSExplorerActionsManager.OPENFILEACTION.getAction());
        }
    }
}