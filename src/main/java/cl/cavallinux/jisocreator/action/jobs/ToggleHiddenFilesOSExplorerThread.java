package cl.cavallinux.jisocreator.action.jobs;

import java.util.Arrays;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;

import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.instances.JFaceResourcesManager;
import cl.cavallinux.jisocreator.instances.OSExplorerActionsManager;
import cl.cavallinux.jisocreator.model.filters.HideHiddenFilesFilter;
import cl.cavallinux.jisocreator.util.ImageUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@lombok.Builder
public class ToggleHiddenFilesOSExplorerThread extends Thread {
    @Override
    public void run() {
        log.info("Toggling show hidden files in OS explorer");
        Action action = OSExplorerActionsManager.SHOWHIDDENFILES.getAction();
        boolean isChecked = action.isChecked();
        execute(isChecked);
        action.setImageDescriptor(loadImageDescriptor(isChecked ? "hidehidden.svg" : "showhidden.svg"));
        OSExplorerActionsManager.REFRESHACTION.getAction().run();
    }

    private ImageDescriptor loadImageDescriptor(String imageName) {
        ImageUtils imageUtils = ImageRegister.INSTANCE.getImageUtils();
        return imageUtils.loadImageDescriptor(imageName);
    }

    private void execute(boolean isChecked) {
        MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
        OSExplorerSashForm osExplorer = mainWindow.getOsExplorer();
        TreeViewer osDirectoriesTree = osExplorer.getOsDirectoriesTree();
        TableViewer osDirectoriesTable = osExplorer.getOsDirectoriesTable();
        if (isChecked) {
            removeFilters(osDirectoriesTree, osDirectoriesTable);
        } else {
            JFaceResourcesManager instance = JFaceResourcesManager.OSEXPLORER_INSTANCE;
            addFilter(instance.getToggleHiddenFilesFilter(), osDirectoriesTree, osDirectoriesTable);
        }
    }
    
    private void addFilter(ViewerFilter filter, StructuredViewer... viewers) {
        Arrays.stream(viewers).forEach(viewer -> viewer.addFilter(filter));
    }
    
    private void removeFilters(StructuredViewer... viewers) {
        Arrays.stream(viewers).forEach(this::removeFilters);
    }
    
    private void removeFilters(StructuredViewer viewer) {
        Arrays.stream(viewer.getFilters()).filter(filter -> filter instanceof HideHiddenFilesFilter).findFirst()
                .ifPresent(filter -> viewer.removeFilter(filter));
    }
}
