package cl.cavallinux.jisocreator.action.jobs;

import java.util.Arrays;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ViewerFilter;

import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.instances.OSExplorerActionsManager;
import cl.cavallinux.jisocreator.model.filters.HideHiddenFilesFilter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        return ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor(imageName);
    }

    private void execute(boolean isChecked) {
        if (isChecked) {
            ViewerFilter[] filters = GUIManager.INSTANCE.getMainWindow().getOsExplorer().getOsDirectoriesTree()
                    .getFilters();
            Arrays.stream(filters).filter(filter -> filter instanceof HideHiddenFilesFilter).findFirst()
                    .ifPresent(filter -> GUIManager.INSTANCE.getMainWindow().getOsExplorer().getOsDirectoriesTree()
                            .removeFilter(filter));
            filters = GUIManager.INSTANCE.getMainWindow().getOsExplorer().getOsDirectoriesTable().getFilters();
            Arrays.stream(filters).filter(filter -> filter instanceof HideHiddenFilesFilter).findFirst()
                    .ifPresent(filter -> GUIManager.INSTANCE.getMainWindow().getOsExplorer().getOsDirectoriesTable()
                            .removeFilter(filter));
        } else {
            GUIManager.INSTANCE.getMainWindow().getOsExplorer().getOsDirectoriesTable()
                    .addFilter(new HideHiddenFilesFilter());
            GUIManager.INSTANCE.getMainWindow().getOsExplorer().getOsDirectoriesTree()
                    .addFilter(new HideHiddenFilesFilter());
        }
    }
}
