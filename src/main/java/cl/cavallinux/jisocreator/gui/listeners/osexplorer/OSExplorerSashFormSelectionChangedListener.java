package cl.cavallinux.jisocreator.gui.listeners.osexplorer;

import java.io.File;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;

import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import cl.cavallinux.jisocreator.instances.OSExplorerActionsManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OSExplorerSashFormSelectionChangedListener implements ISelectionChangedListener {

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        Object eventSource = event.getSource();
        log.info("Selection changed event received: {}",
                ToStringBuilder.reflectionToString(event, ToStringStyle.JSON_STYLE));
        File fileSelected = obtainFileViaSelectionChangedEvent(event);
        if (eventSource instanceof TreeViewer) {
            if (fileSelected == null) {
                OSExplorerActionsManager.ADDFILEACTION.getAction().setEnabled(false);
                OSExplorerActionsManager.GOTOPARENTACTION.getAction().setEnabled(false);
                GUIManager.INSTANCE.getMainWindow().getOsExplorer().getOsDirectoriesTable().setInput(new File(
                        ((File) GUIManager.INSTANCE.getMainWindow().getOsExplorer().getOsDirectoriesTable().getInput())
                                .getParent()));
                GUIManager.INSTANCE.getMainWindow().getOsExplorer().getOsTableText().setText(new File(
                        ((File) GUIManager.INSTANCE.getMainWindow().getOsExplorer().getOsDirectoriesTable().getInput())
                                .getParent())
                        .getAbsolutePath());
                log.warn("SWT Library bug");
                return;
            } else {
                OSExplorerActionsManager.ADDFILEACTION.getAction().setEnabled(true);
            }
            OSExplorerActionsManager.GOTOPARENTACTION.getAction()
                    .setEnabled(!OSAndIsoExplorerManager.INSTANCE.getOsExplorer().isRoot(fileSelected.toPath()));
            GUIManager.INSTANCE.getMainWindow().getOsExplorer().getOsTableText()
                    .setText(fileSelected.getAbsolutePath());
            GUIManager.INSTANCE.getMainWindow().getOsExplorer().getOsDirectoriesTable().setInput(fileSelected);
            OSExplorerActionsManager.OPENFILEACTION.getAction().setEnabled(false);
        } else {
            TableViewer viewer = (TableViewer) eventSource;
            log.info("Event source is a tableviewer: {}",
                    ToStringBuilder.reflectionToString(viewer.getSelection(), ToStringStyle.JSON_STYLE));
            OSExplorerActionsManager.OPENFILEACTION.getAction().setEnabled(true);
        }
    }

    private File obtainFileViaSelectionChangedEvent(SelectionChangedEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        return (File) selection.getFirstElement();
    }
}