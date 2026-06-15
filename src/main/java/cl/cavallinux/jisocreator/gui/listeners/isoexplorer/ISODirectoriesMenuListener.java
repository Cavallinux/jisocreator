package cl.cavallinux.jisocreator.gui.listeners.isoexplorer;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;

import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.IsoExplorerActionsManager;

public class ISODirectoriesMenuListener implements IMenuListener {
    @Override
    public void menuAboutToShow(IMenuManager manager) {
        IStructuredSelection selection = GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTable()
                .getStructuredSelection();
        if (!selection.isEmpty()) {
            manager.add(IsoExplorerActionsManager.OPENISOENTRY.getAction());
            manager.add(IsoExplorerActionsManager.DELETEISOENTRY.getAction());
        }
    }
}