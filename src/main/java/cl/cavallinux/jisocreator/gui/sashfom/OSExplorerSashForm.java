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
import org.eclipse.jface.viewers.IStructuredSelection;
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
import cl.cavallinux.jisocreator.instances.JFaceResourcesManager;
import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import cl.cavallinux.jisocreator.instances.OSExplorerActionsManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class OSExplorerSashForm extends SashForm implements ICompositeCreator {
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
        applyConstraints();
    }

    @Override
    public void addFeatures() {
        log.info("Adding OSExplorerSashForm features");
        setWeights(25, 75);
        fillToolbarAndCoolbars();
        fillTableColumnValues(osDirectoriesTable.getTable());
        addPopMenuToTable(osDirectoriesTable, JFaceResourcesManager.OSEXPLORER_INSTANCE.getDirectoriesMenuListener());
        addJFaceResourcesToControls(JFaceResourcesManager.OSEXPLORER_INSTANCE, osDirectoriesTable, osDirectoriesTree);
        osDirectoriesTree.setInput(OSAndIsoExplorerManager.INSTANCE.getOsExplorer().getRoots());
    }

    @Override
    public void addListeners() {
        log.info("Adding OSExplorerSashForm listeners");
        JFaceResourcesManager osexplorerInstance = JFaceResourcesManager.OSEXPLORER_INSTANCE;
        IDoubleClickListener iDoubleClickListener = osexplorerInstance.getDoubleClickListener();
        ISelectionChangedListener iSelectionChangedListener = osexplorerInstance.getSelectionChangedListener();
        osDirectoriesTree.addDoubleClickListener(iDoubleClickListener);
        osDirectoriesTable.addDoubleClickListener(iDoubleClickListener);

        osDirectoriesTree.addSelectionChangedListener(iSelectionChangedListener);
        osDirectoriesTable.addSelectionChangedListener(iSelectionChangedListener);
    }

    @Override
    public void createComponents() {
        log.info("Adding OSExplorerSashForm components");
        composites = new ArrayList<Composite>();
        composites.add(new Composite(this, SWT.NONE));
        composites.add(new Composite(this, SWT.NONE));

        osTreeCLabel = new CLabel(composites.get(0), SWT.NONE);
        osDirectoriesTree = new TreeViewer(composites.get(0),
                JFaceResourcesManager.OSEXPLORER_INSTANCE.getTreeSWTOptions());

        osTableCoolBar = new CoolBar(composites.get(1), SWT.WRAP | SWT.FLAT);
        osDirectoriesTable = new TableViewer(composites.get(1),
                JFaceResourcesManager.OSEXPLORER_INSTANCE.getTableSWTOptions());
    }

    @Override
    public void applyConstraints() {
        GridDataFactory.defaultsFor(osDirectoriesTree.getControl()).grab(true, true)
                .applyTo(osDirectoriesTree.getControl());
        GridLayoutFactory.fillDefaults().generateLayout(composites.get(0));

        GridDataFactory.defaultsFor(osTableCoolBar).grab(true, false).applyTo(osTableCoolBar);
        GridDataFactory.defaultsFor(osDirectoriesTable.getControl()).grab(true, true)
                .applyTo(osDirectoriesTable.getControl());
        GridLayoutFactory.fillDefaults().generateLayout(composites.get(1));

    }

    private void fillToolbarAndCoolbars() {
        osTreeCLabel.setText("File explorer");
        osTreeCLabel.setImage(ImageRegister.INSTANCE.getImageUtils().loadImage("drive.png"));

        CoolBarManager coolbar = new CoolBarManager(osTableCoolBar);
        ToolBarManager toolbar = new ToolBarManager(SWT.WRAP | SWT.FLAT);

        Arrays.stream(OSExplorerActionsManager.values()).forEach(value -> {
            toolbar.add(value.getAction());
        });

        coolbar.add(toolbar);
        coolbar.update(true);

        CoolItem coolItem = new CoolItem(osTableCoolBar, SWT.WRAP | SWT.FLAT);
        osTableText = new Text(osTableCoolBar, SWT.READ_ONLY | SWT.SINGLE | SWT.BORDER);
        osTableText.pack();
        coolItem.setSize(osTableText.getSize());
        coolItem.setControl(osTableText);
    }

    public IStructuredSelection getTableSelection() {
        return (IStructuredSelection) osDirectoriesTable.getSelection();
    }

    public IStructuredSelection getTreeSelection() {
        return (IStructuredSelection) osDirectoriesTree.getSelection();
    }

    public void refresh() {
        osDirectoriesTree.refresh();
        osDirectoriesTable.refresh();
    }
}