package cl.cavallinux.jisocreator.gui.listeners;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;

import cl.cavallinux.jisocreator.action.isoexplorer.DeleteIsoEntryAction;
import cl.cavallinux.jisocreator.action.isoexplorer.OpenIsoEntryAction;
import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;

public class ISODirectoriesMenuListener implements IMenuListener {
    @Override
    public void menuAboutToShow(IMenuManager manager) {
        IStructuredSelection selection = IsoExplorerSashForm.getInstance().getIsoDirectoriesTable()
                .getStructuredSelection();
        if (!selection.isEmpty()) {
            manager.add(OpenIsoEntryAction.getInstance());
            manager.add(DeleteIsoEntryAction.getInstance());
        }
    }
}