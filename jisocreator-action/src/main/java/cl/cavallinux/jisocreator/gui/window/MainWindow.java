package cl.cavallinux.jisocreator.gui.window;

import org.eclipse.jface.action.ActionContributionItem;
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

import cl.cavallinux.jisocreator.action.main.AboutAction;
import cl.cavallinux.jisocreator.action.main.ExitApplicationAction;
import cl.cavallinux.jisocreator.action.main.NewIsoLayoutAction;
import cl.cavallinux.jisocreator.action.main.OpenIsoLayoutAction;
import cl.cavallinux.jisocreator.action.main.PreferencesAction;
import cl.cavallinux.jisocreator.action.main.SaveAsDropDownMenuAction;
import cl.cavallinux.jisocreator.action.main.SaveAsIsoAction;
import cl.cavallinux.jisocreator.action.main.SaveAsXMLAction;
import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;
import cl.cavallinux.jisocreator.gui.sashfom.OSExplorerSashForm;
import cl.cavallinux.jisocreator.util.ImageUtils;

public class MainWindow extends ApplicationWindow {
    private static MainWindow instance;

    public static MainWindow getInstance() {
	if (instance == null) {
	    instance = new MainWindow();
	}
	return instance;
    }

    private MainWindow(Shell parentShell) {
	super(parentShell);
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
	super.configureShell(shell);
	shell.setText("JIsoCreator");
	shell.setSize(800, 600);
	shell.setImage(ImageUtils.getInstance().loadImage("iso.png"));

	Monitor primary = Display.getCurrent().getPrimaryMonitor();
	Rectangle bounds = primary.getBounds();
	Rectangle rect = shell.getBounds();

	int x = bounds.x + ((bounds.width - rect.width) / 2);
	int y = bounds.y + ((bounds.height - rect.height) / 2);

	shell.setLocation(x, y);
    }

    @Override
    protected Control createContents(Composite parent) {
	Composite composite = new Composite(parent, SWT.NONE);
	SashForm mainPanel = new SashForm(composite, SWT.VERTICAL);
	IsoExplorerSashForm.setInstance(new IsoExplorerSashForm(mainPanel, SWT.HORIZONTAL));
	OSExplorerSashForm.setInstance(new OSExplorerSashForm(mainPanel, SWT.HORIZONTAL));
	GridDataFactory.defaultsFor(mainPanel).grab(true, true).applyTo(mainPanel);
	NewIsoLayoutAction.getInstance().run();
	GridLayoutFactory.swtDefaults().generateLayout(composite);
	return composite;
    }

    @Override
    protected MenuManager createMenuManager() {
	MenuManager mainMenuManager = new MenuManager();

	MenuManager fileMenu = new MenuManager("&File");
	MenuManager toolsMenu = new MenuManager("&Tools");
	MenuManager helpMenu = new MenuManager("&Help");

	mainMenuManager.add(fileMenu);
	mainMenuManager.add(toolsMenu);
	mainMenuManager.add(helpMenu);

	MenuManager saveAsMenu = new MenuManager("&Save as", ImageUtils.getInstance().loadImageDescriptor("saveas.png"),
		"saveAs");
	saveAsMenu.add(SaveAsXMLAction.getInstance());
	saveAsMenu.add(SaveAsIsoAction.getInstance());

	fileMenu.add(NewIsoLayoutAction.getInstance());
	fileMenu.add(OpenIsoLayoutAction.getInstance());
	fileMenu.add(saveAsMenu);
	fileMenu.add(new Separator());
	fileMenu.add(ExitApplicationAction.getInstance());

	toolsMenu.add(PreferencesAction.getInstance());

	helpMenu.add(AboutAction.getInstance());
	return mainMenuManager;
    }

    @Override
    protected ToolBarManager createToolBarManager(int style) {
	ToolBarManager tool = new ToolBarManager(style);
	tool.add(NewIsoLayoutAction.getInstance());
	tool.add(OpenIsoLayoutAction.getInstance());

	ActionContributionItem contrib = new ActionContributionItem(SaveAsDropDownMenuAction.getInstance());
	contrib.setMode(ActionContributionItem.MODE_FORCE_TEXT);
	tool.add(contrib);
	tool.add(new Separator());
	tool.add(PreferencesAction.getInstance());
	tool.add(AboutAction.getInstance());
	return tool;
    }

    @Override
    protected StatusLineManager createStatusLineManager() {
	return new StatusLineManager();
    }

    @Override
    protected void handleShellCloseEvent() {
	ExitApplicationAction.getInstance().run();
    }

    public StatusLineManager getStatusLine() {
	return getStatusLineManager();
    }
}