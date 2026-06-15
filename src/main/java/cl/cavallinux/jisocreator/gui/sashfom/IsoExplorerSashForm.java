package cl.cavallinux.jisocreator.gui.sashfom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Text;

import cl.cavallinux.jisocreator.gui.decl.ICompositeCreator;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.instances.IsoExplorerActionsManager;
import cl.cavallinux.jisocreator.instances.JFaceResourcesManager;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class IsoExplorerSashForm extends SashForm implements ICompositeCreator {
    private List<Composite> composites;
    private TableViewer isoDirectoriesTable;
    private TreeViewer isoDirectoriesTree;
    private CLabel isoTreeCLabel;
    private CoolBar isoTableCoolBar;
    private Text isoTableText;

    public IsoExplorerSashForm(Composite parent, int style) {
        super(parent, style);
        createComponents();
        addFeatures();
        addListeners();
        applyConstraints();
    }

    @Override
    public void addFeatures() {
        log.info("Adding IsoExplorerSashForm features");
        setWeights(new int[] { 25, 75 });
        fillCoolbarAndToolbars();
        fillTableColumnValues(isoDirectoriesTable.getTable());
        addPopMenuToTable(isoDirectoriesTable, JFaceResourcesManager.ISOEXPLORER_INSTANCE.getDirectoriesMenuListener());
        addJFaceResourcesToControls(JFaceResourcesManager.ISOEXPLORER_INSTANCE, isoDirectoriesTable,
                isoDirectoriesTree);
    }

    @Override
    public void addListeners() {
        log.info("Adding IsoExplorerSashForm listeners");
        IDoubleClickListener iDoubleClickListener = JFaceResourcesManager.ISOEXPLORER_INSTANCE.getDoubleClickListener();
        ISelectionChangedListener iSelectionChangedListener = JFaceResourcesManager.ISOEXPLORER_INSTANCE
                .getSelectionChangedListener();
        isoDirectoriesTree.addDoubleClickListener(iDoubleClickListener);
        isoDirectoriesTable.addDoubleClickListener(iDoubleClickListener);

        isoDirectoriesTree.addSelectionChangedListener(iSelectionChangedListener);
        isoDirectoriesTable.addSelectionChangedListener(iSelectionChangedListener);
    }

    @Override
    public void createComponents() {
        log.info("Creating IsoExplorerSashForm components");
        composites = new ArrayList<Composite>();
        composites.add(new Composite(this, SWT.NONE));
        composites.add(new Composite(this, SWT.NONE));

        isoTreeCLabel = new CLabel(composites.get(0), SWT.NONE);
        isoDirectoriesTree = new TreeViewer(composites.get(0),
                JFaceResourcesManager.ISOEXPLORER_INSTANCE.getTreeSWTOptions());

        isoTableCoolBar = new CoolBar(composites.get(1), SWT.WRAP | SWT.FLAT);
        isoDirectoriesTable = new TableViewer(composites.get(1),
                JFaceResourcesManager.ISOEXPLORER_INSTANCE.getTableSWTOptions());
    }

    @Override
    public void addJFaceResourcesToControls(JFaceResourcesManager resourceManager, TableViewer table, TreeViewer tree) {
        tree.setContentProvider(resourceManager.getTreeContentProvider());
        tree.setLabelProvider(resourceManager.getTreeLabelProvider());
        tree.addFilter(resourceManager.getShowOnlyDirectoriesFilter());
        tree.setComparator(resourceManager.getDirectoriesComparator());

        table.setContentProvider(resourceManager.getTableProviderAdapter());
        table.setLabelProvider(resourceManager.getTableProviderAdapter());
        table.setComparator(resourceManager.getDirectoriesComparator());
    }

    @Override
    public void applyConstraints() {
        GridDataFactory.defaultsFor(isoTreeCLabel).grab(true, false).applyTo(isoTreeCLabel);
        GridDataFactory.defaultsFor(isoDirectoriesTree.getControl()).grab(true, true)
                .applyTo(isoDirectoriesTree.getControl());
        GridLayoutFactory.fillDefaults().generateLayout(composites.get(0));

        GridDataFactory.defaultsFor(isoTableCoolBar).grab(true, false).applyTo(isoTableCoolBar);
        GridDataFactory.defaultsFor(isoDirectoriesTable.getControl()).grab(true, true)
                .applyTo(isoDirectoriesTable.getControl());
        GridLayoutFactory.fillDefaults().generateLayout(composites.get(1));
    }

    private void fillCoolbarAndToolbars() {
        isoTreeCLabel.setText("Iso explorer");
        isoTreeCLabel.setImage(ImageRegister.INSTANCE.getImageUtils().loadImage("iso.png"));
        CoolBarManager coolbar = new CoolBarManager(isoTableCoolBar);
        ToolBarManager toolbar = new ToolBarManager(SWT.WRAP | SWT.FLAT);
        Arrays.stream(IsoExplorerActionsManager.values()).forEach(value -> {
            toolbar.add(value.getAction());
        });
        coolbar.add(toolbar);
        coolbar.update(true);
        CoolItem coolItem = new CoolItem(isoTableCoolBar, SWT.WRAP | SWT.FLAT);
        isoTableText = new Text(isoTableCoolBar, SWT.READ_ONLY | SWT.SINGLE | SWT.BORDER);
        isoTableText.pack();
        coolItem.setSize(isoTableText.getSize());
        coolItem.setControl(isoTableText);
    }

    public void refresh(ITreeNode node) {
        TreeViewer isoTreeInstance = getIsoDirectoriesTree();
        TableViewer isoTableInstance = getIsoDirectoriesTable();
        isoTreeInstance.setSelection(isoTableInstance.getSelection());
        isoTreeInstance.expandToLevel(node, 1);
    }

    public void refresh() {
        getIsoDirectoriesTree().refresh();
        getIsoDirectoriesTable().refresh();
    }
}