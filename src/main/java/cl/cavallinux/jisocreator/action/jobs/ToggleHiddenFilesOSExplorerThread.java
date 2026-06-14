package cl.cavallinux.jisocreator.action.jobs;

import java.util.Arrays;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ViewerFilter;

import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
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
            ViewerFilter[] filters = OSExplorerSashForm.getInstance().getOsDirectoriesTree().getFilters();
            Arrays.stream(filters).filter(filter -> filter instanceof HideHiddenFilesFilter).findFirst()
                    .ifPresent(filter -> OSExplorerSashForm.getInstance().getOsDirectoriesTree().removeFilter(filter));
            filters = OSExplorerSashForm.getInstance().getOsDirectoriesTable().getFilters();
            Arrays.stream(filters).filter(filter -> filter instanceof HideHiddenFilesFilter).findFirst()
                    .ifPresent(filter -> OSExplorerSashForm.getInstance().getOsDirectoriesTable().removeFilter(filter));
        } else {
            OSExplorerSashForm.getInstance().getOsDirectoriesTable().addFilter(new HideHiddenFilesFilter());
            OSExplorerSashForm.getInstance().getOsDirectoriesTree().addFilter(new HideHiddenFilesFilter());
        }
    }
}
