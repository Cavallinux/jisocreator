package cl.cavallinux.jisocreator.gui.listeners.osexplorer;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;

import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.OSExplorerActionsManager;
import lombok.Builder;

@Builder
public class OSDirectoriesMenuListener implements IMenuListener {
    @Override
    public void menuAboutToShow(IMenuManager manager) {
        MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
        OSExplorerSashForm osExplorer = mainWindow.getOsExplorer();
        IStructuredSelection selection = osExplorer.getOsDirectoriesTable().getStructuredSelection();
        if (!selection.isEmpty()) {
            manager.add(OSExplorerActionsManager.ADDFILEACTION.getAction());
            manager.add(OSExplorerActionsManager.OPENFILEACTION.getAction());
        }
    }
}