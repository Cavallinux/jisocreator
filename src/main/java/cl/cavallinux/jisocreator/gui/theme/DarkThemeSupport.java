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

import cl.cavallinux.jisocreator.instances.IOManager;

public final class DarkThemeSupport {
    private static final String SWT_WIN32_DARK_MODE_EXPLORER_THEME =
            "org.eclipse.swt.internal.win32.useDarkModeExplorerTheme";
    private static final String SWT_WIN32_DARK_MODE_SHELL_TITLE =
            "org.eclipse.swt.internal.win32.useShellTitleColoring";
    private static final String DARK_PALETTE_KEY = "jisocreator.darktheme.palette";
    private static final String THEME_MODE_PREFERENCE_KEY = "jisocreator.theme.mode";
    private static final String THEME_MODE_LIGHT = "LIGHT";
    private static final String THEME_MODE_DARK = "DARK";

    private static final int[] DARK_BG_RGB = { 30, 30, 30 };
    private static final int[] DARK_PANEL_RGB = { 37, 37, 38 };
    private static final int[] DARK_INPUT_RGB = { 45, 45, 48 };
    private static final int[] DARK_INPUT_FOCUS_RGB = { 55, 55, 60 };
    private static final int[] DARK_INPUT_FOCUS_BORDER_RGB = { 100, 150, 200 };
    private static final int[] DARK_INPUT_DISABLED_RGB = { 35, 35, 35 };
    private static final int[] DARK_FG_RGB = { 240, 240, 240 };
    private static final int[] DARK_FG_DISABLED_RGB = { 120, 120, 120 };

    private DarkThemeSupport() {
    }

    public static void enableWindowsDarkMode(Display display) {
        if (Objects.isNull(display) || !isDarkModeActive()) {
            return;
        }

        display.setData(SWT_WIN32_DARK_MODE_EXPLORER_THEME, Boolean.TRUE);
        display.setData(SWT_WIN32_DARK_MODE_SHELL_TITLE, Boolean.TRUE);
    }

    public static void applyToControlTree(Control control) {
        if (Objects.isNull(control) || control.isDisposed() || !isDarkModeActive()) {
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
        if (Objects.isNull(shell) || shell.isDisposed() || !isDarkModeActive()) {
            return;
        }

        Palette palette = getOrCreatePalette(shell);
        applyMenuBarStyling(shell, palette);
        if (Objects.nonNull(toolBar) && !toolBar.isDisposed()) {
            applyToolBar(toolBar, palette);
        }
        shell.setBackground(palette.background);
        shell.setForeground(palette.foreground);
    }

    /**
     * Applies dark theme styling to the menu bar. This method handles
     * the menu bar styling which requires special consideration since
     * SWT Menu objects may not support all color operations on all platforms.
     */
    private static void applyMenuBarStyling(Shell shell, Palette palette) {
        Menu menuBar = shell.getMenuBar();
        if (Objects.isNull(menuBar) || menuBar.isDisposed()) {
            return;
        }

        applyMenuRecursively(menuBar, palette);
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

        if (control instanceof Text text) {
            text.setBackground(palette.inputBackground);
            attachFocusListeners(text, palette);
            attachDisabledListener(text, palette);
            return;
        }

        if (control instanceof Combo combo) {
            combo.setBackground(palette.inputBackground);
            attachFocusListeners(combo, palette);
            attachDisabledListener(combo, palette);
            return;
        }

        if (control instanceof Spinner spinner) {
            spinner.setBackground(palette.inputBackground);
            attachFocusListeners(spinner, palette);
            attachDisabledListener(spinner, palette);
            return;
        }

        if (control instanceof Tree || control instanceof List) {
            control.setBackground(palette.inputBackground);
            attachFocusListeners(control, palette);
            attachDisabledListener(control, palette);
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

        // Apply background and foreground to the menu
        setOptionalColor(menu, "setBackground", palette.panelBackground);
        setOptionalColor(menu, "setForeground", palette.foreground);

        // Apply styling to each menu item in the menu
        for (MenuItem menuItem : menu.getItems()) {
            if (!menuItem.isDisposed()) {
                // Try to set colors for menu items
                setOptionalColor(menuItem, "setBackground", palette.panelBackground);
                setOptionalColor(menuItem, "setForeground", palette.foreground);

                // Recursively apply to submenus
                Menu submenu = menuItem.getMenu();
                if (Objects.nonNull(submenu) && !submenu.isDisposed()) {
                    applyMenuRecursively(submenu, palette);
                }
            }
        }
    }

    private static void applyToolBar(ToolBar toolBar, Palette palette) {
        if (Objects.isNull(toolBar) || toolBar.isDisposed()) {
            return;
        }

        toolBar.setBackground(palette.panelBackground);
        toolBar.setForeground(palette.foreground);
    }

    private static void attachFocusListeners(Control control, Palette palette) {
        if (Objects.isNull(control) || control.isDisposed()) {
            return;
        }

        control.addListener(SWT.FocusIn, event -> {
            if (!control.isDisposed() && control.getEnabled()) {
                control.setBackground(palette.inputBackgroundFocus);
            }
        });

        control.addListener(SWT.FocusOut, event -> {
            if (!control.isDisposed() && control.getEnabled()) {
                control.setBackground(palette.inputBackground);
            }
        });
    }

    private static void attachDisabledListener(Control control, Palette palette) {
        if (Objects.isNull(control) || control.isDisposed()) {
            return;
        }

        control.addListener(SWT.Modify, event -> {
            if (control.isDisposed()) {
                return;
            }
            updateControlStateStyle(control, palette);
        });

        // Initial state setup
        updateControlStateStyle(control, palette);
    }

    private static void updateControlStateStyle(Control control, Palette palette) {
        if (control.isDisposed()) {
            return;
        }

        if (!control.getEnabled()) {
            control.setBackground(palette.inputBackgroundDisabled);
            control.setForeground(palette.foregroundDisabled);
        } else if (control.isFocusControl()) {
            control.setBackground(palette.inputBackgroundFocus);
            control.setForeground(palette.foreground);
        } else {
            control.setBackground(palette.inputBackground);
            control.setForeground(palette.foreground);
        }
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

    /**
     * Determina si el tema oscuro debe aplicarse efectivamente, combinando la
     * plataforma nativa con la preferencia de usuario {@code jisocreator.theme.mode}
     * (AUTO/LIGHT/DARK). En modo AUTO (o valor no reconocido/ausente), se consulta el
     * tema real de Windows via {@link WindowsSystemThemeDetector}.
     */
    private static boolean isDarkModeActive() {
        if (!isWin32()) {
            return false;
        }

        String themeMode = IOManager.INSTANCE.getIoUtils().getStore().getString(THEME_MODE_PREFERENCE_KEY);
        if (THEME_MODE_LIGHT.equals(themeMode)) {
            return false;
        }
        if (THEME_MODE_DARK.equals(themeMode)) {
            return true;
        }
        return WindowsSystemThemeDetector.isSystemDarkMode();
    }

    private static void setOptionalColor(Object target, String methodName, Color color) {
        if (Objects.isNull(target) || Objects.isNull(color) || color.isDisposed()) {
            return;
        }

        try {
            Method method = target.getClass().getMethod(methodName, Color.class);
            method.invoke(target, color);
        } catch (ReflectiveOperationException ignored) {
            // Compatibility fallback for SWT variants without this API.
            // This is expected for some widgets/menus depending on platform and SWT version
        }
    }

    private static final class Palette {
        private final Color background;
        private final Color panelBackground;
        private final Color inputBackground;
        private final Color inputBackgroundFocus;
        private final Color inputBackgroundDisabled;
        private final Color foreground;
        private final Color foregroundDisabled;

        private Palette(Display display) {
            background = new Color(display, DARK_BG_RGB[0], DARK_BG_RGB[1], DARK_BG_RGB[2]);
            panelBackground = new Color(display, DARK_PANEL_RGB[0], DARK_PANEL_RGB[1], DARK_PANEL_RGB[2]);
            inputBackground = new Color(display, DARK_INPUT_RGB[0], DARK_INPUT_RGB[1], DARK_INPUT_RGB[2]);
            inputBackgroundFocus = new Color(display, DARK_INPUT_FOCUS_RGB[0], DARK_INPUT_FOCUS_RGB[1], DARK_INPUT_FOCUS_RGB[2]);
            inputBackgroundDisabled = new Color(display, DARK_INPUT_DISABLED_RGB[0], DARK_INPUT_DISABLED_RGB[1], DARK_INPUT_DISABLED_RGB[2]);
            foreground = new Color(display, DARK_FG_RGB[0], DARK_FG_RGB[1], DARK_FG_RGB[2]);
            foregroundDisabled = new Color(display, DARK_FG_DISABLED_RGB[0], DARK_FG_DISABLED_RGB[1], DARK_FG_DISABLED_RGB[2]);
        }

        private boolean isDisposed() {
            return background.isDisposed() || panelBackground.isDisposed() || inputBackground.isDisposed()
                    || inputBackgroundFocus.isDisposed() || inputBackgroundDisabled.isDisposed()
                    || foreground.isDisposed() || foregroundDisabled.isDisposed();
        }

        private void dispose() {
            disposeColor(background);
            disposeColor(panelBackground);
            disposeColor(inputBackground);
            disposeColor(inputBackgroundFocus);
            disposeColor(inputBackgroundDisabled);
            disposeColor(foreground);
            disposeColor(foregroundDisabled);
        }

        private void disposeColor(Color color) {
            if (Objects.nonNull(color) && !color.isDisposed()) {
                color.dispose();
            }
        }
    }
}
