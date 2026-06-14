package cl.cavallinux.jisocreator.instances;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;

import cl.cavallinux.jisocreator.gui.listeners.ISODirectoriesMenuListener;
import cl.cavallinux.jisocreator.gui.listeners.OSDirectoriesMenuListener;
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
    OSEXPLORER_INSTANCE(new HideHiddenFilesFilter(), new ShowOnlyDirectoriesFilter(), new OSDirectoriesComparator(),
            new OSTreeLabelProvider(), new OSTreeContentProvider(), new OsTableProvider(),
            new OSDirectoriesMenuListener(),
            SWT.VIRTUAL | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI,
            SWT.VIRTUAL | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI),
    ISOEXPLORER_INSTANCE(null, new ShowOnlyIsoDirectoriesFilter(), new ITreeNodeDirectoriesFirstComparator(),
            new IsoTreeLabelProvider(), new IsoTreeContentProvider(), new IsoTableProvider(),
            new ISODirectoriesMenuListener(),
            SWT.VIRTUAL | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI,
            SWT.VIRTUAL | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);

    private ViewerFilter toggleHiddenFilesFilter;
    private ViewerFilter showOnlyDirectoriesFilter;
    private ViewerComparator directoriesComparator;
    private TreeLabelAdapter treeLabelProvider;
    private TreeContentAdapter treeContentProvider;
    private TableProviderAdapter tableProviderAdapter;
    private IMenuListener directoriesMenuListener;
    private int treeSWTOptions;
    private int tableSWTOptions;
}