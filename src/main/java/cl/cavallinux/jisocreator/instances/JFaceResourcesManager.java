package cl.cavallinux.jisocreator.instances;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;

import cl.cavallinux.jisocreator.gui.listeners.OSDirectoriesMenuListener;
import cl.cavallinux.jisocreator.model.comparators.OSDirectoriesComparator;
import cl.cavallinux.jisocreator.model.filters.HideHiddenFilesFilter;
import cl.cavallinux.jisocreator.model.filters.ShowOnlyDirectoriesFilter;
import cl.cavallinux.jisocreator.model.providers.decl.TableProviderAdapter;
import cl.cavallinux.jisocreator.model.providers.decl.TreeContentAdapter;
import cl.cavallinux.jisocreator.model.providers.decl.TreeLabelAdapter;
import cl.cavallinux.jisocreator.model.providers.impl.osexplorer.OSTreeContentProvider;
import cl.cavallinux.jisocreator.model.providers.impl.osexplorer.OSTreeLabelProvider;
import cl.cavallinux.jisocreator.model.providers.impl.osexplorer.OsTableProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JFaceResourcesManager {
    INSTANCE(new HideHiddenFilesFilter(), new ShowOnlyDirectoriesFilter(), new OSDirectoriesComparator(),
            new OSTreeLabelProvider(), new OSTreeContentProvider(), new OsTableProvider(), new OSDirectoriesMenuListener());

    private ViewerFilter toggleHiddenFilesFilter;
    private ViewerFilter showOnlyDirectoriesFilter;
    private ViewerComparator osDirectoriesComparator;
    private TreeLabelAdapter osTreeLabelProvider;
    private TreeContentAdapter osTreeContentProvider;
    private TableProviderAdapter osTableProviderAdapter;
    private IMenuListener osDirectoriesMenuListener;
}