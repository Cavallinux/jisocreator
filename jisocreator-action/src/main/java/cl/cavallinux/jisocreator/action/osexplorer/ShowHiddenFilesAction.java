package cl.cavallinux.jisocreator.action.osexplorer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Display;

import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.model.filters.HideHiddenFilesFilter;
import cl.cavallinux.jisocreator.util.ImageUtils;

public class ShowHiddenFilesAction extends Action {
    private static ShowHiddenFilesAction instance;

    private ShowHiddenFilesAction() {
        super("Show hidden", IAction.AS_CHECK_BOX);
        setImageDescriptor(ImageUtils.getInstance().loadImageDescriptor("showhidden.svg"));
        setToolTipText("Show hidden files of the OS explorer");
    }

    @Override
    public void run() {
        Display.getCurrent().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (ShowHiddenFilesAction.this.isChecked()) {
                    setImageDescriptor(ImageUtils.getInstance().loadImageDescriptor("hidehidden.svg"));
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
                    setImageDescriptor(ImageUtils.getInstance().loadImageDescriptor("showhidden.svg"));
                    OSExplorerSashForm.getInstance().getOsDirectoriesTable().addFilter(new HideHiddenFilesFilter());
                    OSExplorerSashForm.getInstance().getOsDirectoriesTree().addFilter(new HideHiddenFilesFilter());
                }
                RefreshExplorerAction.getInstance().run();

            }
        });
    }

    public static ShowHiddenFilesAction getInstance() {
        if (instance == null) {
            instance = new ShowHiddenFilesAction();
        }
        return instance;
    }
}