package cl.cavallinux.jisocreator.gui.sashfom;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.MenuManager;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import cl.cavallinux.jisocreator.gui.decl.ICompositeCreator;
import cl.cavallinux.jisocreator.gui.listeners.ISODirectoriesMenuListener;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.instances.IsoExplorerActionsManager;
import cl.cavallinux.jisocreator.model.comparators.ITreeNodeDirectoriesFirstComparator;
import cl.cavallinux.jisocreator.model.filters.isoexplorer.ShowOnlyIsoDirectoriesFilter;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.model.providers.impl.isoexplorer.IsoTableProvider;
import cl.cavallinux.jisocreator.model.providers.impl.isoexplorer.IsoTreeContentProvider;
import cl.cavallinux.jisocreator.model.providers.impl.isoexplorer.IsoTreeLabelProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class IsoExplorerSashForm extends SashForm implements ICompositeCreator {
    private static IsoExplorerSashForm instance;
    private List<Composite> composites;
    private TableViewer isoDirectoriesTable;
    private TreeViewer isoDirectoriesTree;
    private CLabel isoTreeCLabel;
    private CoolBar isoTableCoolBar;
    private Text isoTableText;

    public IsoExplorerSashForm(Composite arg0, int arg1) {
        super(arg0, arg1);
        createComponents();
        addFeatures();
        addListeners();
    }

    @Override
    public void addFeatures() {
        log.info("Adding IsoExplorerSashForm features");
        setWeights(new int[] { 25, 75 });
        isoTreeCLabel.setText("Iso explorer");
        isoTreeCLabel.setImage(ImageRegister.INSTANCE.getImageUtils().loadImage("iso.png"));
        CoolBarManager coolbar = new CoolBarManager(isoTableCoolBar);
        ToolBarManager toolbar = new ToolBarManager(SWT.WRAP | SWT.FLAT);

        toolbar.add(IsoExplorerActionsManager.OPENISOENTRY.getAction());
        toolbar.add(IsoExplorerActionsManager.GOTOISOPARENT.getAction());
        toolbar.add(IsoExplorerActionsManager.SHOWISOINFO.getAction());
        toolbar.add(IsoExplorerActionsManager.DELETEISOENTRY.getAction());

        coolbar.add(toolbar);
        coolbar.update(true);
        CoolItem coolItem = new CoolItem(isoTableCoolBar, SWT.WRAP | SWT.FLAT);
        isoTableText = new Text(isoTableCoolBar, SWT.READ_ONLY | SWT.SINGLE | SWT.BORDER);
        isoTableText.pack();
        coolItem.setSize(isoTableText.getSize());
        coolItem.setControl(isoTableText);
        
        Map<String, String> columnTooltips = new LinkedHashMap<>();
        columnTooltips.put("Name", "File name");
        columnTooltips.put("Type", "File type");
        columnTooltips.put("Size", "File size, in bytes");
        columnTooltips.put("Last Modified Date", "File last modified date");
        
        columnTooltips.forEach((columnName, tooltip) -> {
            TableColumn tvc = new TableColumn(isoDirectoriesTable.getTable(), SWT.LEFT);
            tvc.setText(columnName);
            tvc.setToolTipText(tooltip);
            tvc.setWidth(200);
            tvc.setMoveable(true);
            tvc.setResizable(true);
        });

        isoDirectoriesTable.getTable().setHeaderVisible(true);
        isoDirectoriesTree.setContentProvider(new IsoTreeContentProvider());
        isoDirectoriesTree.setLabelProvider(new IsoTreeLabelProvider());
        isoDirectoriesTree.addFilter(new ShowOnlyIsoDirectoriesFilter());
        isoDirectoriesTree.setComparator(new ITreeNodeDirectoriesFirstComparator());

        MenuManager isoDirectoriesTableMenuManager = new MenuManager();
        isoDirectoriesTableMenuManager.setRemoveAllWhenShown(true);
        isoDirectoriesTableMenuManager.addMenuListener(new ISODirectoriesMenuListener());
        Control isoDirectoriesTableControl = isoDirectoriesTable.getControl();
        isoDirectoriesTableControl.setMenu(isoDirectoriesTableMenuManager.createContextMenu(isoDirectoriesTableControl));
        isoDirectoriesTable.setContentProvider(new IsoTableProvider());
        isoDirectoriesTable.setLabelProvider(new IsoTableProvider());
        isoDirectoriesTable.setComparator(new ITreeNodeDirectoriesFirstComparator());

        GridDataFactory.defaultsFor(isoTreeCLabel).grab(true, false).applyTo(isoTreeCLabel);
        GridDataFactory.defaultsFor(isoDirectoriesTree.getControl()).grab(true, true)
                .applyTo(isoDirectoriesTree.getControl());
        GridLayoutFactory.fillDefaults().generateLayout(composites.get(0));

        GridDataFactory.defaultsFor(isoTableCoolBar).grab(true, false).applyTo(isoTableCoolBar);
        GridDataFactory.defaultsFor(isoDirectoriesTable.getControl()).grab(true, true)
                .applyTo(isoDirectoriesTable.getControl());
        GridLayoutFactory.fillDefaults().generateLayout(composites.get(1));
    }

    @Override
    public void addListeners() {
        log.info("Adding IsoExplorerSashForm listeners");
        Action openAction = IsoExplorerActionsManager.OPENISOENTRY.getAction();
        IDoubleClickListener iDoubleClickListener = (IDoubleClickListener) openAction;
        ISelectionChangedListener iSelectionChangedListener = (ISelectionChangedListener) openAction;
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
                SWT.VIRTUAL | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);

        isoTableCoolBar = new CoolBar(composites.get(1), SWT.WRAP | SWT.FLAT);
        isoDirectoriesTable = new TableViewer(composites.get(1),
                SWT.VIRTUAL | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
    }
    
    public void refresh(ITreeNode node) {
        TreeViewer isoTreeInstance = getIsoDirectoriesTree();
        TableViewer isoTableInstance = getIsoDirectoriesTable();
        isoTreeInstance.setSelection(isoTableInstance.getSelection());
        isoTreeInstance.expandToLevel(node, 1);
    }

    public static void setInstance(IsoExplorerSashForm instance) {
        IsoExplorerSashForm.instance = instance;
    }

    public static IsoExplorerSashForm getInstance() {
        return instance;
    }
}