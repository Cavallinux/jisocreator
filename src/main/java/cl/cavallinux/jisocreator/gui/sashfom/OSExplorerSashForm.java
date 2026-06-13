package cl.cavallinux.jisocreator.gui.sashfom;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
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

import cl.cavallinux.jisocreator.action.osexplorer.AddFileAction;
import cl.cavallinux.jisocreator.action.osexplorer.GoToParentAction;
import cl.cavallinux.jisocreator.action.osexplorer.OpenAction;
import cl.cavallinux.jisocreator.action.osexplorer.RefreshExplorerAction;
import cl.cavallinux.jisocreator.action.osexplorer.ShowHiddenFilesAction;
import cl.cavallinux.jisocreator.gui.decl.ICompositeCreator;
import cl.cavallinux.jisocreator.gui.listeners.OSDirectoriesMenuListener;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.comparators.OSDirectoriesComparator;
import cl.cavallinux.jisocreator.model.filters.HideHiddenFilesFilter;
import cl.cavallinux.jisocreator.model.filters.ShowOnlyDirectoriesFilter;
import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;
import cl.cavallinux.jisocreator.model.providers.impl.osexplorer.OSTreeContentProvider;
import cl.cavallinux.jisocreator.model.providers.impl.osexplorer.OSTreeLabelProvider;
import cl.cavallinux.jisocreator.model.providers.impl.osexplorer.OsTableProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class OSExplorerSashForm extends SashForm implements ICompositeCreator {
    private static OSExplorerSashForm instance;
    private List<Composite> composites;
    private TableViewer osDirectoriesTable;
    private TreeViewer osDirectoriesTree;
    private CLabel osTreeCLabel;
    private CoolBar osTableCoolBar;
    private Text osTableText;

    public OSExplorerSashForm(Composite parent, int style) {
        super(parent, style);
        createComponents();
        addFeatures();
        addListeners();
    }

    @Override
    public void addFeatures() {
        log.info("Adding OSExplorerSashForm features");
        setWeights(25, 75);
        osTreeCLabel.setText("File explorer");
        osTreeCLabel.setImage(ImageRegister.INSTANCE.getImageUtils().loadImage("drive.png"));

        CoolBarManager coolbar = new CoolBarManager(osTableCoolBar);
        ToolBarManager toolbar = new ToolBarManager(SWT.WRAP | SWT.FLAT);

        toolbar.add(OpenAction.getInstance());
        toolbar.add(GoToParentAction.getInstance());
        toolbar.add(RefreshExplorerAction.getInstance());
        toolbar.add(AddFileAction.getInstance());
        toolbar.add(ShowHiddenFilesAction.getInstance());

        coolbar.add(toolbar);
        coolbar.update(true);

        CoolItem coolItem = new CoolItem(osTableCoolBar, SWT.WRAP | SWT.FLAT);
        osTableText = new Text(osTableCoolBar, SWT.READ_ONLY | SWT.SINGLE | SWT.BORDER);
        osTableText.pack();
        coolItem.setSize(osTableText.getSize());
        coolItem.setControl(osTableText);
        
        Map<String, String> columnTooltips = LinkedHashMap.newLinkedHashMap(4);
        columnTooltips.put("Name", "File name");
        columnTooltips.put("Type", "File type");
        columnTooltips.put("Size", "File size, in bytes");
        columnTooltips.put("Last Modified Date", "File last modified date");
        
        columnTooltips.forEach((columnName, tooltip) -> {
            TableColumn tvc = new TableColumn(osDirectoriesTable.getTable(), SWT.LEFT);
            tvc.setText(columnName);
            tvc.setToolTipText(tooltip);
            tvc.setWidth(200);
            tvc.setMoveable(true);
            tvc.setResizable(true);
        });

        osDirectoriesTable.getTable().setHeaderVisible(true);

        osDirectoriesTree.setContentProvider(new OSTreeContentProvider());
        osDirectoriesTree.setLabelProvider(new OSTreeLabelProvider());
        osDirectoriesTree.addFilter(new ShowOnlyDirectoriesFilter());
        osDirectoriesTree.addFilter(new HideHiddenFilesFilter());
        osDirectoriesTree.setComparator(new OSDirectoriesComparator());
        osDirectoriesTree.setInput(OSExplorer.getInstance());

        MenuManager osDirectoriesTableMenuManager = new MenuManager();
        osDirectoriesTableMenuManager.setRemoveAllWhenShown(true);
        osDirectoriesTableMenuManager.addMenuListener(new OSDirectoriesMenuListener());
        Control osDirectoriesTableControl = osDirectoriesTable.getControl();
        osDirectoriesTableControl.setMenu(osDirectoriesTableMenuManager.createContextMenu(osDirectoriesTableControl));
        osDirectoriesTable.setContentProvider(new OsTableProvider());
        osDirectoriesTable.setLabelProvider(new OsTableProvider());
        osDirectoriesTable.addFilter(new HideHiddenFilesFilter());
        osDirectoriesTable.setComparator(new OSDirectoriesComparator());

        GridDataFactory.defaultsFor(osTreeCLabel).grab(true, false).applyTo(osTreeCLabel);
        GridDataFactory.defaultsFor(osDirectoriesTree.getControl()).grab(true, true)
                .applyTo(osDirectoriesTree.getControl());
        GridLayoutFactory.fillDefaults().generateLayout(composites.get(0));

        GridDataFactory.defaultsFor(osTableCoolBar).grab(true, false).applyTo(osTableCoolBar);
        GridDataFactory.defaultsFor(osDirectoriesTableControl).grab(true, true).applyTo(osDirectoriesTableControl);
        GridLayoutFactory.fillDefaults().generateLayout(composites.get(1));
    }

    @Override
    public void addListeners() {
        log.info("Adding OSExplorerSashForm listeners");
        osDirectoriesTree.addDoubleClickListener(OpenAction.getInstance());
        osDirectoriesTable.addDoubleClickListener(OpenAction.getInstance());

        osDirectoriesTree.addSelectionChangedListener(OpenAction.getInstance());
        osDirectoriesTable.addSelectionChangedListener(OpenAction.getInstance());
    }

    @Override
    public void createComponents() {
        log.info("Adding OSExplorerSashForm components");
        composites = new ArrayList<Composite>();
        composites.add(new Composite(this, SWT.NONE));
        composites.add(new Composite(this, SWT.NONE));

        osTreeCLabel = new CLabel(composites.get(0), SWT.NONE);
        osDirectoriesTree = new TreeViewer(composites.get(0),
                SWT.VIRTUAL | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);

        osTableCoolBar = new CoolBar(composites.get(1), SWT.WRAP | SWT.FLAT);
        osDirectoriesTable = new TableViewer(composites.get(1),
                SWT.VIRTUAL | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
    }
    
    public static void setInstance(OSExplorerSashForm instance) {
        OSExplorerSashForm.instance = instance;
    }

    public static OSExplorerSashForm getInstance() {
        return instance;
    }
}