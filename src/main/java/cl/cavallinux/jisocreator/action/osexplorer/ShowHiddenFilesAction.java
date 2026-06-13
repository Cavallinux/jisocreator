package cl.cavallinux.jisocreator.action.osexplorer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Display;

import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.instances.OSExplorerActionsManager;
import cl.cavallinux.jisocreator.model.filters.HideHiddenFilesFilter;

public class ShowHiddenFilesAction extends Action {
    public ShowHiddenFilesAction() {
        super("Show hidden", IAction.AS_CHECK_BOX);
        setImageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("showhidden.svg"));
        setToolTipText("Show hidden files of the OS explorer");
    }

    @Override
    public void run() {
        Display.getCurrent().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (ShowHiddenFilesAction.this.isChecked()) {
                    setImageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("hidehidden.svg"));
                    ViewerFilter[] filters = OSExplorerSashForm.getInstance().getOsDirectoriesTree().getFilters();
                    for (ViewerFilter filter : filters) {
                        if (filter instanceof HideHiddenFilesFilter) {
                            OSExplorerSashForm.getInstance().getOsDirectoriesTree().removeFilter(filter);
                            break;
                        }
                    }
                    filters = OSExplorerSashForm.getInstance().getOsDirectoriesTable().getFilters();
                    for (ViewerFilter filter : filters) {
                        if (filter instanceof HideHiddenFilesFilter) {
                            OSExplorerSashForm.getInstance().getOsDirectoriesTable().removeFilter(filter);
                            break;
                        }
                    }
                } else {
                    setImageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("showhidden.svg"));
                    OSExplorerSashForm.getInstance().getOsDirectoriesTable().addFilter(new HideHiddenFilesFilter());
                    OSExplorerSashForm.getInstance().getOsDirectoriesTree().addFilter(new HideHiddenFilesFilter());
                }
                OSExplorerActionsManager.REFRESHACTION.getAction().run();
            }
        });
    }
}