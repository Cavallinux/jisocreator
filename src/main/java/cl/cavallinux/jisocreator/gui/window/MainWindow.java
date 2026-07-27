package cl.cavallinux.jisocreator.gui.window;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
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

import cl.cavallinux.jisocreator.action.main.LoadCommandLineISOLayoutAction;
import cl.cavallinux.jisocreator.gui.i18n.MainWindowMessages;
import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;
import cl.cavallinux.jisocreator.gui.theme.DarkThemeSupport;
import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.instances.ActionsManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class MainWindow extends ApplicationWindow {
    private IContributionItem separator;
    private OSExplorerSashForm osExplorer;
    private IsoExplorerSashForm isoExplorer;
    private String isoFilePath;

    private MainWindow(Shell parentShell) {
        super(parentShell);
        Display display = Objects.nonNull(parentShell) ? parentShell.getDisplay() : Display.getDefault();
        DarkThemeSupport.enableWindowsDarkMode(display);
        separator = new Separator();
        addMenuBar();
        addToolBar(SWT.RIGHT);
        addStatusLine();
    }
    
    @Builder
    protected MainWindow() {
        this(null);
    }

    @Override
    protected boolean showTopSeperator() {
        /**
         * ApplicationWindow's top separator is a native Label(SWT.SEPARATOR) that Win32
         * paints via DrawEdge(EDGE_ETCHED), using the system 3D highlight/shadow colors
         * directly - it ignores setBackground()/setForeground() entirely (same class of
         * native-chrome limitation as TabFolder's themed body), so in dark mode it always
         * renders as a bright etched line between the menu bar and the toolbar. Hiding it
         * when dark mode is active is the only way to remove that artifact.
         */
        return !DarkThemeSupport.isDarkModeActive() && super.showTopSeperator();
    }

    @Override
    protected void configureShell(Shell shell) {
        log.info("Configuring shell");
        super.configureShell(shell);
        Display.setAppName("jisocreator");
        shell.setText(MainWindowMessages.windowTitle);
        shell.setSize(1024, 768);
        shell.setImage(ImageRegister.INSTANCE.getImageUtils().loadImage("jisocreator.svg"));

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
        Composite composite = (Composite) super.createContents(parent);
        SashForm mainPanel = new SashForm(composite, SWT.VERTICAL);
        isoExplorer = new IsoExplorerSashForm(mainPanel, SWT.HORIZONTAL);
        osExplorer = new OSExplorerSashForm(mainPanel, SWT.HORIZONTAL);
        loadIsoLayout(isoFilePath);
        osExplorer.setInitialSelection();
        GridDataFactory.defaultsFor(mainPanel).grab(true, true).applyTo(mainPanel);
        GridLayoutFactory.swtDefaults().generateLayout(composite);
        applyDarkTheme(composite);
        /** Apply theme to menu bar after it's fully initialized */
        Display.getCurrent().asyncExec(() -> applyDarkThemeToMenuBar());
        return composite;
    }

    private void applyDarkTheme(Composite composite) {
        DarkThemeSupport.applyToControlTree(composite);
        Shell shell = composite.getShell();
        DarkThemeSupport.applyToMainBars(shell, getToolBarManager().getControl());
        applyDarkThemeToStatusLine();
        shell.getDisplay().asyncExec(() -> {
            DarkThemeSupport.applyToMainBars(shell, getToolBarManager().getControl());
            applyDarkThemeToStatusLine();
        });
    }

    private void applyDarkThemeToMenuBar() {
        Shell shell = getShell();
        if (Objects.nonNull(shell) && !shell.isDisposed()) {
            DarkThemeSupport.applyToMainBars(shell, getToolBarManager().getControl());
        }
    }

    private void applyDarkThemeToStatusLine() {
        if (Objects.isNull(getStatusLineManager())) {
            return;
        }

        Control statusLineControl = getStatusLineManager().getControl();
        if (Objects.nonNull(statusLineControl)) {
            DarkThemeSupport.applyToControlTree(statusLineControl);
        }
    }

    private void loadIsoLayout(String isoFilePath) {
        if (StringUtils.isNotBlank(isoFilePath)) {
            LoadCommandLineISOLayoutAction action = (LoadCommandLineISOLayoutAction) ActionsManager.LOADISOFROMLAYOUT
                    .getAction();
            action.run(isoFilePath);
        } else {
            ActionsManager.NEWISOLAYOUTACTION.getAction().run();
        }
    }

    @Override
    protected MenuManager createMenuManager() {
        log.info("creating menu managers");
        MenuManager mainMenuManager = super.createMenuManager();

        MenuManager fileMenu = new MenuManager(MainWindowMessages.fileMenuName);
        MenuManager toolsMenu = new MenuManager(MainWindowMessages.toolsMenuName);
        MenuManager helpMenu = new MenuManager(MainWindowMessages.helpMenuName);

        mainMenuManager.add(fileMenu);
        mainMenuManager.add(toolsMenu);
        mainMenuManager.add(helpMenu);

        MenuManager saveAsMenu = new MenuManager(MainWindowMessages.fileSaveAsName,
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
        ToolBarManager tool = super.createToolBarManager(style);
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

    public int open(String file) {
        log.info("Opening window with file: {}", file);
        this.isoFilePath = file;
        return super.open();
    }

    private Monitor determinateActiveMonitor() {
        Display display = Display.getCurrent();
        Shell activeShell = display.getActiveShell();
        return Objects.nonNull(activeShell) ? activeShell.getMonitor() : display.getPrimaryMonitor();
    }

    @Override
    protected StatusLineManager createStatusLineManager() {
        log.info("Creating status line manager");
        return super.createStatusLineManager();
    }

    @Override
    protected void handleShellCloseEvent() {
        ActionsManager.EXITACTION.getAction().run();
    }
    
    @Override
    public void setStatus(String message) {
        super.setStatus(message);
    }

    public IProgressMonitor getProgressMonitor() {
        return getStatusLineManager().getProgressMonitor();
    }
    
    public void setStatusLineActiveCancelButton(boolean activeCancelButton) {
        getStatusLineManager().setCancelEnabled(activeCancelButton);
    }

    public void setVisible(boolean shellVisible) {
        Shell windowShell = getShell();
        if (Objects.nonNull(windowShell)) {
            windowShell.setVisible(shellVisible);
        }
    }
}