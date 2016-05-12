package cl.cavallinux.jisocreator.gui.sashfom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
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
import cl.cavallinux.jisocreator.model.filters.HideHiddenFilesFilter;
import cl.cavallinux.jisocreator.model.filters.ShowOnlyDirectoriesFilter;
import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;
import cl.cavallinux.jisocreator.model.providers.impl.osexplorer.OSTreeContentProvider;
import cl.cavallinux.jisocreator.model.providers.impl.osexplorer.OSTreeLabelProvider;
import cl.cavallinux.jisocreator.model.providers.impl.osexplorer.OsTableProvider;
import cl.cavallinux.jisocreator.model.sorters.SortByDirectoriesFirstSorter;
import cl.cavallinux.jisocreator.util.ImageUtils;

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
	setWeights(new int[] { 25, 75 });
	osTreeCLabel.setText("File explorer");
	osTreeCLabel.setImage(ImageUtils.getInstance().loadImage("drive.png"));

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

	List<String> tooltips = new ArrayList<String>();
	{
	    tooltips.add("File name");
	    tooltips.add("File size, in bytes");
	    tooltips.add("File type");
	    tooltips.add("File kast modified date");
	}

	List<String> columnNames = new ArrayList<String>();
	{
	    columnNames.add("Name");
	    columnNames.add("Size");
	    columnNames.add("Type");
	    columnNames.add("Last Modified Date");
	}
	Iterator<String> it = tooltips.iterator();

	for (String columnName : columnNames) {
	    TableColumn tvc = new TableColumn(osDirectoriesTable.getTable(), SWT.LEFT);
	    tvc.setText(columnName);
	    tvc.setToolTipText(it.next());
	    tvc.setWidth(200);
	    tvc.setMoveable(true);
	    tvc.setResizable(true);
	}
	osDirectoriesTable.getTable().setHeaderVisible(true);

	osDirectoriesTree.setContentProvider(new OSTreeContentProvider());
	osDirectoriesTree.setLabelProvider(new OSTreeLabelProvider());
	osDirectoriesTree.addFilter(new ShowOnlyDirectoriesFilter());
	osDirectoriesTree.addFilter(new HideHiddenFilesFilter());
	osDirectoriesTree.setSorter(new SortByDirectoriesFirstSorter());
	osDirectoriesTree.setInput(OSExplorer.getInstance());

	osDirectoriesTable.setContentProvider(new OsTableProvider());
	osDirectoriesTable.setLabelProvider(new OsTableProvider());
	osDirectoriesTable.addFilter(new HideHiddenFilesFilter());
	osDirectoriesTable.setSorter(new SortByDirectoriesFirstSorter());

	GridDataFactory.defaultsFor(osTreeCLabel).grab(true, false).applyTo(osTreeCLabel);
	GridDataFactory.defaultsFor(osDirectoriesTree.getControl()).grab(true, true)
		.applyTo(osDirectoriesTree.getControl());
	GridLayoutFactory.fillDefaults().generateLayout(composites.get(0));

	GridDataFactory.defaultsFor(osTableCoolBar).grab(true, false).applyTo(osTableCoolBar);
	GridDataFactory.defaultsFor(osDirectoriesTable.getControl()).grab(true, true)
		.applyTo(osDirectoriesTable.getControl());
	GridLayoutFactory.fillDefaults().generateLayout(composites.get(1));
    }

    @Override
    public void addListeners() {
	osDirectoriesTree.addDoubleClickListener(OpenAction.getInstance());
	osDirectoriesTable.addDoubleClickListener(OpenAction.getInstance());

	osDirectoriesTree.addSelectionChangedListener(OpenAction.getInstance());
	osDirectoriesTable.addSelectionChangedListener(OpenAction.getInstance());
    }

    @Override
    public void createComponents() {
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

    public TableViewer getOsDirectoriesTable() {
	return osDirectoriesTable;
    }

    public void setOsDirectoriesTable(TableViewer osDirectoriesTable) {
	this.osDirectoriesTable = osDirectoriesTable;
    }

    public TreeViewer getOsDirectoriesTree() {
	return osDirectoriesTree;
    }

    public void setOsDirectoriesTree(TreeViewer osDirectoriesTree) {
	this.osDirectoriesTree = osDirectoriesTree;
    }

    public Text getOsTableText() {
	return osTableText;
    }

    public void setOsTableText(Text osTableText) {
	this.osTableText = osTableText;
    }

    public static void setInstance(OSExplorerSashForm instance) {
	OSExplorerSashForm.instance = instance;
    }

    public static OSExplorerSashForm getInstance() {
	return instance;
    }
}