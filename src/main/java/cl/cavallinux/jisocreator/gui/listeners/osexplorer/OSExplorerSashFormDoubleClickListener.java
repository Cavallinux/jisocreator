package cl.cavallinux.jisocreator.gui.listeners.osexplorer;

import java.io.File;
import java.util.EventObject;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import cl.cavallinux.jisocreator.action.osexplorer.OpenAction;
import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.OSExplorerActionsManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OSExplorerSashFormDoubleClickListener implements IDoubleClickListener {

    @Override
    public void doubleClick(DoubleClickEvent event) {
        Object eventSource = event.getSource();
        log.info("Double click event received: {}, event source: {}",
                ToStringBuilder.reflectionToString(event, ToStringStyle.JSON_STYLE),
                ToStringBuilder.reflectionToString(eventSource, ToStringStyle.JSON_STYLE));
        shootDoubleClick(event, obtainFileViaDoubleClickEvent(event));
    }

    private File obtainFileViaDoubleClickEvent(DoubleClickEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        return (File) selection.getFirstElement();
    }

    private void shootDoubleClick(EventObject event, File fileSelected) {
        if (event.getSource() instanceof TreeViewer) {
            updateOSExplorerTree(fileSelected);
        } else {
            runOSExplorerOpenAction(fileSelected);
        }
    }

    private void runOSExplorerOpenAction(File fileSelected) {
        OpenAction action = (OpenAction) OSExplorerActionsManager.OPENFILEACTION.getAction();
        action.setFile(fileSelected);
        action.run();
    }

    private void updateOSExplorerTree(File fileSelected) {
        OSExplorerSashForm osExplorer = GUIManager.INSTANCE.getMainWindow().getOsExplorer();
        if (osExplorer.getOsDirectoriesTree().getExpandedState(fileSelected)) {
            osExplorer.getOsDirectoriesTree().collapseToLevel(fileSelected, 1);
        } else {
            osExplorer.getOsDirectoriesTree().expandToLevel(fileSelected, 1);
        }
    }
}
