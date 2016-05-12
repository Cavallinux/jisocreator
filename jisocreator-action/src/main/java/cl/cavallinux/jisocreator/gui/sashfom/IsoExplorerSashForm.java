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

import cl.cavallinux.jisocreator.action.isoexplorer.DeleteIsoEntryAction;
import cl.cavallinux.jisocreator.action.isoexplorer.GoToIsoEntryParentAction;
import cl.cavallinux.jisocreator.action.isoexplorer.OpenIsoEntryAction;
import cl.cavallinux.jisocreator.action.isoexplorer.ShowIsoInformationAction;
import cl.cavallinux.jisocreator.gui.decl.ICompositeCreator;
import cl.cavallinux.jisocreator.model.filters.isoexplorer.ShowOnlyIsoDirectoriesFilter;
import cl.cavallinux.jisocreator.model.providers.impl.isoexplorer.IsoTableProvider;
import cl.cavallinux.jisocreator.model.providers.impl.isoexplorer.IsoTreeContentProvider;
import cl.cavallinux.jisocreator.model.providers.impl.isoexplorer.IsoTreeLabelProvider;
import cl.cavallinux.jisocreator.model.sorters.SortByIsoEntryFirstSorter;
import cl.cavallinux.jisocreator.util.ImageUtils;

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
	setWeights(new int[] { 25, 75 });
	isoTreeCLabel.setText("Iso explorer");
	isoTreeCLabel.setImage(ImageUtils.getInstance().loadImage("iso.png"));
	CoolBarManager coolbar = new CoolBarManager(isoTableCoolBar);
	ToolBarManager toolbar = new ToolBarManager(SWT.WRAP | SWT.FLAT);

	toolbar.add(OpenIsoEntryAction.getInstance());
	toolbar.add(GoToIsoEntryParentAction.getInstance());
	toolbar.add(ShowIsoInformationAction.getInstance());
	toolbar.add(DeleteIsoEntryAction.getInstance());

	coolbar.add(toolbar);
	coolbar.update(true);
	CoolItem coolItem = new CoolItem(isoTableCoolBar, SWT.WRAP | SWT.FLAT);
	isoTableText = new Text(isoTableCoolBar, SWT.READ_ONLY | SWT.SINGLE | SWT.BORDER);
	isoTableText.pack();
	coolItem.setSize(isoTableText.getSize());
	coolItem.setControl(isoTableText);

	List<String> columnNames = new ArrayList<String>();
	{
	    columnNames.add("Name");
	    columnNames.add("Size");
	    columnNames.add("Type");
	    columnNames.add("Last Modified Date");
	}

	List<String> tooltips = new ArrayList<String>();
	{
	    tooltips.add("Name");
	    tooltips.add("Size");
	    tooltips.add("Type");
	    tooltips.add("Last Modified Date");
	}
	Iterator<String> it = tooltips.iterator();

	for (String columnName : columnNames) {
	    TableColumn tvc = new TableColumn(isoDirectoriesTable.getTable(), SWT.LEFT);
	    tvc.setText(columnName);
	    tvc.setToolTipText(it.next());
	    tvc.setWidth(200);
	    tvc.setMoveable(true);
	    tvc.setResizable(true);
	}

	isoDirectoriesTable.getTable().setHeaderVisible(true);
	isoDirectoriesTree.setContentProvider(new IsoTreeContentProvider());
	isoDirectoriesTree.setLabelProvider(new IsoTreeLabelProvider());
	isoDirectoriesTree.addFilter(new ShowOnlyIsoDirectoriesFilter());
	isoDirectoriesTree.setSorter(new SortByIsoEntryFirstSorter());

	isoDirectoriesTable.setContentProvider(new IsoTableProvider());
	isoDirectoriesTable.setLabelProvider(new IsoTableProvider());
	isoDirectoriesTable.setSorter(new SortByIsoEntryFirstSorter());

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
	isoDirectoriesTree.addDoubleClickListener(OpenIsoEntryAction.getInstance());
	isoDirectoriesTable.addDoubleClickListener(OpenIsoEntryAction.getInstance());

	isoDirectoriesTree.addSelectionChangedListener(OpenIsoEntryAction.getInstance());
	isoDirectoriesTable.addSelectionChangedListener(OpenIsoEntryAction.getInstance());
    }

    @Override
    public void createComponents() {
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

    public TableViewer getIsoDirectoriesTable() {
	return isoDirectoriesTable;
    }

    public void setIsoDirectoriesTable(TableViewer isoDirectoriesTable) {
	this.isoDirectoriesTable = isoDirectoriesTable;
    }

    public TreeViewer getIsoDirectoriesTree() {
	return isoDirectoriesTree;
    }

    public void setIsoDirectoriesTree(TreeViewer isoDirectoriesTree) {
	this.isoDirectoriesTree = isoDirectoriesTree;
    }

    public Text getIsoTableText() {
	return isoTableText;
    }

    public void setIsoTableText(Text isoTableText) {
	this.isoTableText = isoTableText;
    }

    public static void setInstance(IsoExplorerSashForm instance) {
	IsoExplorerSashForm.instance = instance;
    }

    public static IsoExplorerSashForm getInstance() {
	return instance;
    }
}