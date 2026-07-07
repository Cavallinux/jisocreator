package cl.cavallinux.jisocreator.instances;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import cl.cavallinux.jisocreator.gui.decl.ICompositeCreator;
import cl.cavallinux.jisocreator.gui.listeners.isoexplorer.ISODirectoriesMenuListener;
import cl.cavallinux.jisocreator.gui.listeners.isoexplorer.ISOExplorerSashFormDoubleClickListener;
import cl.cavallinux.jisocreator.gui.listeners.isoexplorer.ISOExplorerSashFormSelectionChangedListener;
import cl.cavallinux.jisocreator.gui.listeners.osexplorer.OSDirectoriesMenuListener;
import cl.cavallinux.jisocreator.gui.listeners.osexplorer.OSExplorerSashFormDoubleClickListener;
import cl.cavallinux.jisocreator.gui.listeners.osexplorer.OSExplorerSashFormSelectionChangedListener;
import cl.cavallinux.jisocreator.model.comparators.ITreeNodeDirectoriesFirstComparator;
import cl.cavallinux.jisocreator.model.comparators.OSDirectoriesComparator;
import cl.cavallinux.jisocreator.model.filters.HideHiddenFilesFilter;
import cl.cavallinux.jisocreator.model.filters.ShowOnlyDirectoriesFilter;
import cl.cavallinux.jisocreator.model.filters.isoexplorer.ShowOnlyIsoDirectoriesFilter;
import cl.cavallinux.jisocreator.model.providers.decl.TableProviderAdapter;
import cl.cavallinux.jisocreator.model.providers.decl.TreeContentAdapter;
import cl.cavallinux.jisocreator.model.providers.decl.TreeLabelAdapter;
import cl.cavallinux.jisocreator.model.providers.impl.isoexplorer.IsoTableProvider;
import cl.cavallinux.jisocreator.model.providers.impl.isoexplorer.IsoTreeContentProvider;
import cl.cavallinux.jisocreator.model.providers.impl.isoexplorer.IsoTreeLabelProvider;
import cl.cavallinux.jisocreator.model.providers.impl.osexplorer.OSTreeContentProvider;
import cl.cavallinux.jisocreator.model.providers.impl.osexplorer.OSTreeLabelProvider;
import cl.cavallinux.jisocreator.model.providers.impl.osexplorer.OsTableProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JFaceResourcesManager {
    OSEXPLORER_INSTANCE(HideHiddenFilesFilter.builder().build(), ShowOnlyDirectoriesFilter.builder().build(),
            OSDirectoriesComparator.builder().build(), OSTreeLabelProvider.builder().build(),
            OSTreeContentProvider.builder().build(), OsTableProvider.builder().build(),
            OSDirectoriesMenuListener.builder().build(),ICompositeCreator.COMPOSITE_SWT_OPTIONS, 
            ICompositeCreator.COMPOSITE_SWT_OPTIONS,
            OSExplorerSashFormDoubleClickListener.builder().build(), 
            OSExplorerSashFormSelectionChangedListener.builder().build()),
    ISOEXPLORER_INSTANCE(null, ShowOnlyIsoDirectoriesFilter.builder().build(), new ITreeNodeDirectoriesFirstComparator(),
            new IsoTreeLabelProvider(), new IsoTreeContentProvider(), new IsoTableProvider(),
            new ISODirectoriesMenuListener(), ICompositeCreator.COMPOSITE_SWT_OPTIONS,
            ICompositeCreator.COMPOSITE_SWT_OPTIONS,
            new ISOExplorerSashFormDoubleClickListener(), new ISOExplorerSashFormSelectionChangedListener());

    private ViewerFilter toggleHiddenFilesFilter;
    private ViewerFilter showOnlyDirectoriesFilter;
    private ViewerComparator directoriesComparator;
    private TreeLabelAdapter treeLabelProvider;
    private TreeContentAdapter treeContentProvider;
    private TableProviderAdapter tableProviderAdapter;
    private IMenuListener directoriesMenuListener;
    private int treeSWTOptions;
    private int tableSWTOptions;
    private IDoubleClickListener doubleClickListener;
    private ISelectionChangedListener selectionChangedListener;
}