package cl.cavallinux.jisocreator.gui.theme;

import java.lang.reflect.Method;
import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;

public final class DarkThemeSupport {
    private static final String SWT_WIN32_DARK_MODE_EXPLORER_THEME =
            "org.eclipse.swt.internal.win32.useDarkModeExplorerTheme";
    private static final String SWT_WIN32_DARK_MODE_SHELL_TITLE =
            "org.eclipse.swt.internal.win32.useShellTitleColoring";
    private static final String DARK_PALETTE_KEY = "jisocreator.darktheme.palette";

    private static final int[] DARK_BG_RGB = { 30, 30, 30 };
    private static final int[] DARK_PANEL_RGB = { 37, 37, 38 };
    private static final int[] DARK_INPUT_RGB = { 45, 45, 48 };
    private static final int[] DARK_FG_RGB = { 240, 240, 240 };

    private DarkThemeSupport() {
    }

    public static void enableWindowsDarkMode(Display display) {
        if (Objects.isNull(display) || !isWin32()) {
            return;
        }

        display.setData(SWT_WIN32_DARK_MODE_EXPLORER_THEME, Boolean.TRUE);
        display.setData(SWT_WIN32_DARK_MODE_SHELL_TITLE, Boolean.TRUE);
    }

    public static void applyToControlTree(Control control) {
        if (Objects.isNull(control) || control.isDisposed() || !isWin32()) {
            return;
        }

        Shell shell = control.getShell();
        Palette palette = getOrCreatePalette(shell);
        applyControlRecursively(control, palette);
        applyMenuRecursively(shell.getMenuBar(), palette);

        shell.setBackground(palette.background);
        shell.setForeground(palette.foreground);
    }

    public static void applyToMainBars(Shell shell, ToolBar toolBar) {
        if (Objects.isNull(shell) || shell.isDisposed() || !isWin32()) {
            return;
        }

        Palette palette = getOrCreatePalette(shell);
        applyMenuRecursively(shell.getMenuBar(), palette);
        applyToolBar(toolBar, palette);
        shell.setBackground(palette.background);
        shell.setForeground(palette.foreground);
    }

    private static void applyControlRecursively(Control control, Palette palette) {
        if (Objects.isNull(control) || control.isDisposed()) {
            return;
        }

        applyControlColors(control, palette);

        if (control instanceof Composite composite) {
            for (Control child : composite.getChildren()) {
                applyControlRecursively(child, palette);
            }
        }
    }

    private static void applyControlColors(Control control, Palette palette) {
        control.setForeground(palette.foreground);

        if (control instanceof Table table) {
            table.setBackground(palette.inputBackground);
            setOptionalColor(table, "setHeaderBackground", palette.panelBackground);
            setOptionalColor(table, "setHeaderForeground", palette.foreground);
            return;
        }

        if (control instanceof Text || control instanceof Combo || control instanceof Spinner
                || control instanceof Tree || control instanceof List) {
            control.setBackground(palette.inputBackground);
            return;
        }

        if (control instanceof Button || control instanceof Label || control instanceof Group || control instanceof SashForm
                || control instanceof TabFolder || control instanceof Composite) {
            control.setBackground(palette.panelBackground);
            return;
        }

        control.setBackground(palette.background);
    }

    private static void applyMenuRecursively(Menu menu, Palette palette) {
        if (Objects.isNull(menu) || menu.isDisposed()) {
            return;
        }

        setOptionalColor(menu, "setBackground", palette.panelBackground);
        setOptionalColor(menu, "setForeground", palette.foreground);

        for (MenuItem menuItem : menu.getItems()) {
            applyMenuRecursively(menuItem.getMenu(), palette);
        }
    }

    private static void applyToolBar(ToolBar toolBar, Palette palette) {
        if (Objects.isNull(toolBar) || toolBar.isDisposed()) {
            return;
        }

        toolBar.setBackground(palette.panelBackground);
        toolBar.setForeground(palette.foreground);
    }

    private static Palette getOrCreatePalette(Shell shell) {
        Object shellData = shell.getData(DARK_PALETTE_KEY);
        if (shellData instanceof Palette palette && !palette.isDisposed()) {
            return palette;
        }

        Palette palette = new Palette(shell.getDisplay());
        shell.setData(DARK_PALETTE_KEY, palette);
        shell.addListener(SWT.Dispose, event -> {
            Object disposeData = shell.getData(DARK_PALETTE_KEY);
            if (disposeData instanceof Palette disposePalette) {
                disposePalette.dispose();
                shell.setData(DARK_PALETTE_KEY, null);
            }
        });
        return palette;
    }

    private static boolean isWin32() {
        return "win32".equals(SWT.getPlatform());
    }

    private static void setOptionalColor(Object target, String methodName, Color color) {
        try {
            Method method = target.getClass().getMethod(methodName, Color.class);
            method.invoke(target, color);
        } catch (ReflectiveOperationException ignored) {
            // Compatibility fallback for SWT variants without this API.
        }
    }

    private static final class Palette {
        private final Color background;
        private final Color panelBackground;
        private final Color inputBackground;
        private final Color foreground;

        private Palette(Display display) {
            background = new Color(display, DARK_BG_RGB[0], DARK_BG_RGB[1], DARK_BG_RGB[2]);
            panelBackground = new Color(display, DARK_PANEL_RGB[0], DARK_PANEL_RGB[1], DARK_PANEL_RGB[2]);
            inputBackground = new Color(display, DARK_INPUT_RGB[0], DARK_INPUT_RGB[1], DARK_INPUT_RGB[2]);
            foreground = new Color(display, DARK_FG_RGB[0], DARK_FG_RGB[1], DARK_FG_RGB[2]);
        }

        private boolean isDisposed() {
            return background.isDisposed() || panelBackground.isDisposed() || inputBackground.isDisposed()
                    || foreground.isDisposed();
        }

        private void dispose() {
            disposeColor(background);
            disposeColor(panelBackground);
            disposeColor(inputBackground);
            disposeColor(foreground);
        }

        private void disposeColor(Color color) {
            if (Objects.nonNull(color) && !color.isDisposed()) {
                color.dispose();
            }
        }
    }
}
