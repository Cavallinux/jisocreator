package cl.cavallinux.jisocreator.gui.listeners.osexplorer;

import java.io.File;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;

import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import cl.cavallinux.jisocreator.instances.OSExplorerActionsManager;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class OSExplorerSashFormSelectionChangedListener implements ISelectionChangedListener {

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        Object eventSource = event.getSource();
        log.debug("Selection changed event received: {}",
                ToStringBuilder.reflectionToString(event, ToStringStyle.JSON_STYLE));
        File fileSelected = obtainFileViaSelectionChangedEvent(event);
        if (eventSource instanceof TreeViewer) {
            MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
            if (fileSelected == null) {
                OSExplorerActionsManager.ADDFILEACTION.getAction().setEnabled(false);
                OSExplorerActionsManager.GOTOPARENTACTION.getAction().setEnabled(false);
                mainWindow.getOsExplorer().getOsDirectoriesTable().setInput(new File(
                        ((File) mainWindow.getOsExplorer().getOsDirectoriesTable().getInput())
                                .getParent()));
                mainWindow.getOsExplorer().getOsTableText().setText(new File(
                        ((File) mainWindow.getOsExplorer().getOsDirectoriesTable().getInput())
                                .getParent())
                        .getAbsolutePath());
                log.warn("SWT Library bug");
                return;
            } else {
                OSExplorerActionsManager.ADDFILEACTION.getAction().setEnabled(true);
            }
            OSExplorerActionsManager.GOTOPARENTACTION.getAction()
                    .setEnabled(!OSAndIsoExplorerManager.INSTANCE.getOsExplorer().isRoot(fileSelected.toPath()));
            mainWindow.getOsExplorer().getOsTableText()
                    .setText(fileSelected.getAbsolutePath());
            mainWindow.getOsExplorer().getOsDirectoriesTable().setInput(fileSelected);
            OSExplorerActionsManager.OPENFILEACTION.getAction().setEnabled(false);
        } else {
            TableViewer viewer = (TableViewer) eventSource;
            log.debug("Event source is a tableviewer: {}",
                    ToStringBuilder.reflectionToString(viewer.getSelection(), ToStringStyle.JSON_STYLE));
            OSExplorerActionsManager.OPENFILEACTION.getAction().setEnabled(true);
        }
    }

    private File obtainFileViaSelectionChangedEvent(SelectionChangedEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        return (File) selection.getFirstElement();
    }
}