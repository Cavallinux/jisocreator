package cl.cavallinux.jisocreator.gui.window;

import java.util.Objects;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;
import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.instances.ActionsManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class MainWindow extends ApplicationWindow {
    private IContributionItem separator;
    private OSExplorerSashForm osExplorer;
    private IsoExplorerSashForm isoExplorer;

    private MainWindow(Shell parentShell) {
        super(parentShell);
        separator = new Separator();
        addMenuBar();
        addToolBar(SWT.RIGHT);
        addStatusLine();
        showTopSeperator();
    }

    public MainWindow() {
        this(null);
    }

    @Override
    protected void configureShell(Shell shell) {
        log.info("Configuring shell");
        super.configureShell(shell);
        shell.setText("JIsoCreator");
        shell.setSize(1024, 768);
        shell.setImage(ImageRegister.INSTANCE.getImageUtils().loadImage("iso.png"));

        Monitor primary = determinateActiveMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = shell.getBounds();

        int x = bounds.x + ((bounds.width - rect.width) / 2);
        int y = bounds.y + ((bounds.height - rect.height) / 2);

        shell.setLocation(x, y);
    }

    @Override
    protected Control createContents(Composite parent) {
        log.info("Creating main window contents");
        Composite composite = new Composite(parent, SWT.NONE);
        SashForm mainPanel = new SashForm(composite, SWT.VERTICAL);
        isoExplorer = new IsoExplorerSashForm(mainPanel, SWT.HORIZONTAL);
        osExplorer = new OSExplorerSashForm(mainPanel, SWT.HORIZONTAL);
        ActionsManager.NEWISOLAYOUTACTION.getAction().run();
        GridDataFactory.defaultsFor(mainPanel).grab(true, true).applyTo(mainPanel);
        GridLayoutFactory.swtDefaults().generateLayout(composite);
        return composite;
    }

    @Override
    protected MenuManager createMenuManager() {
        log.info("creating menu managers");
        MenuManager mainMenuManager = new MenuManager();

        MenuManager fileMenu = new MenuManager("&File");
        MenuManager toolsMenu = new MenuManager("&Tools");
        MenuManager helpMenu = new MenuManager("&Help");

        mainMenuManager.add(fileMenu);
        mainMenuManager.add(toolsMenu);
        mainMenuManager.add(helpMenu);

        MenuManager saveAsMenu = new MenuManager("&Save as",
                ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("saveas.png"), "saveAs");
        saveAsMenu.add(ActionsManager.SAVEASXMLACTION.getAction());
        saveAsMenu.add(ActionsManager.SAVEASISOACTION.getAction());

        fileMenu.add(ActionsManager.NEWISOLAYOUTACTION.getAction());
        fileMenu.add(ActionsManager.OPENISOLAYOUTACTION.getAction());
        fileMenu.add(saveAsMenu);
        fileMenu.add(separator);
        fileMenu.add(ActionsManager.EXITACTION.getAction());

        toolsMenu.add(ActionsManager.PREFERENCESACTION.getAction());

        helpMenu.add(ActionsManager.ABOUTACTION.getAction());
        return mainMenuManager;
    }

    @Override
    protected ToolBarManager createToolBarManager(int style) {
        log.info("creating tool bar manager");
        ToolBarManager tool = new ToolBarManager(style);
        tool.add(ActionsManager.NEWISOLAYOUTACTION.getAction());
        tool.add(ActionsManager.OPENISOLAYOUTACTION.getAction());
        tool.add(separator);
        tool.add(ActionsManager.SAVEASISOACTION.getAction());
        tool.add(ActionsManager.SAVEASXMLACTION.getAction());
        tool.add(separator);
        tool.add(ActionsManager.PREFERENCESACTION.getAction());
        tool.add(ActionsManager.ABOUTACTION.getAction());
        return tool;
    }
    
    private Monitor determinateActiveMonitor() {
        Display display = Display.getCurrent();
        Shell activeShell = display.getActiveShell();
        return Objects.nonNull(activeShell) ? activeShell.getMonitor() : display.getPrimaryMonitor();
    }

    @Override
    protected StatusLineManager createStatusLineManager() {
        return new StatusLineManager();
    }

    @Override
    protected void handleShellCloseEvent() {
        ActionsManager.EXITACTION.getAction().run();
    }
    
    public IProgressMonitor getProgressMonitor() {
        return getStatusLineManager().getProgressMonitor();
    }
}