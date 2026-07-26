package cl.cavallinux.jisocreator.gui.listeners.osexplorer;

import java.io.File;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;

import cl.cavallinux.jisocreator.action.jobs.LoadOSDirectoryContentsThread;
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
                File parentDirectory = new File(
                        ((File) mainWindow.getOsExplorer().getOsDirectoriesTable().getInput()).getParent());
                mainWindow.getOsExplorer().getOsTableText().setText(parentDirectory.getAbsolutePath());
                loadDirectoryContents(mainWindow, parentDirectory);
                log.warn("SWT Library bug");
                return;
            } else {
                OSExplorerActionsManager.ADDFILEACTION.getAction().setEnabled(true);
            }
            OSExplorerActionsManager.GOTOPARENTACTION.getAction()
                    .setEnabled(!OSAndIsoExplorerManager.INSTANCE.getOsExplorer().isRoot(fileSelected.toPath()));
            mainWindow.getOsExplorer().getOsTableText()
                    .setText(fileSelected.getAbsolutePath());
            loadDirectoryContents(mainWindow, fileSelected);
            OSExplorerActionsManager.OPENFILEACTION.getAction().setEnabled(false);
        } else {
            TableViewer viewer = (TableViewer) eventSource;
            log.debug("Event source is a tableviewer: {}",
                    ToStringBuilder.reflectionToString(viewer.getSelection(), ToStringStyle.JSON_STYLE));
            OSExplorerActionsManager.OPENFILEACTION.getAction().setEnabled(true);
        }
    }

    /**
     * Populates the OS directories table with the contents of the given directory.
     * <p>
     * The directory scan and file-metadata pre-fetch is performed off the SWT UI
     * thread by {@link LoadOSDirectoryContentsThread}, which then marshals the
     * actual {@code TableViewer#setInput(Object)} call back onto the UI thread
     * once the metadata is already warmed, keeping the UI responsive while
     * navigating directories with a large number of entries.
     * </p>
     * 
     * @param mainWindow the main window whose OS explorer table should be updated
     * @param directory  the directory whose contents should be displayed
     */
    private void loadDirectoryContents(MainWindow mainWindow, File directory) {
        TableViewer osDirectoriesTable = mainWindow.getOsExplorer().getOsDirectoriesTable();
        LoadOSDirectoryContentsThread.builder().directory(directory).tableViewer(osDirectoriesTable).build().start();
    }

    private File obtainFileViaSelectionChangedEvent(SelectionChangedEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        return (File) selection.getFirstElement();
    }
}