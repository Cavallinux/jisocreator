package cl.cavallinux.jisocreator.gui.theme;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests para {@link WindowsSystemThemeDetector}.
 *
 * <p>Solo se ejercita {@link WindowsSystemThemeDetector#parseIsDarkMode(String)}, la
 * logica pura de parseo de la salida de {@code reg query}. Deliberadamente no se invoca
 * {@link WindowsSystemThemeDetector#isSystemDarkMode()} (que si ejecuta {@code reg.exe}):
 * esa llamada dependeria del host de ejecucion real (Windows) y romperia la convencion
 * del proyecto de mantener los tests portables e independientes del sistema operativo
 * del runner de CI (ver {@code SwtPlatformAssumptions}). Al ser parseo de texto puro,
 * sin ninguna dependencia de SWT/Display/reg.exe, estos tests corren identicos en
 * cualquier plataforma y nunca se omiten (skip).</p>
 */
@DisplayName("WindowsSystemThemeDetector tests")
class WindowsSystemThemeDetectorTest {

    @Test
    @DisplayName("Should report dark mode when AppsUseLightTheme is 0x0")
    void shouldReportDarkModeWhenAppsUseLightThemeIsZero() {
        String regQueryOutput = """
                HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize
                    AppsUseLightTheme    REG_DWORD    0x0
                """;

        assertTrue(WindowsSystemThemeDetector.parseIsDarkMode(regQueryOutput));
    }

    @Test
    @DisplayName("Should report light mode when AppsUseLightTheme is 0x1")
    void shouldReportLightModeWhenAppsUseLightThemeIsOne() {
        String regQueryOutput = """
                HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize
                    AppsUseLightTheme    REG_DWORD    0x1
                """;

        assertFalse(WindowsSystemThemeDetector.parseIsDarkMode(regQueryOutput));
    }

    @Test
    @DisplayName("Should fall back to light mode when the registry value is missing")
    void shouldFallBackToLightModeWhenValueMissing() {
        String regQueryOutput = "ERROR: The system was unable to find the specified registry key or value.\r\n";

        assertFalse(WindowsSystemThemeDetector.parseIsDarkMode(regQueryOutput));
    }

    @Test
    @DisplayName("Should fall back to light mode for null output")
    void shouldFallBackToLightModeForNullOutput() {
        assertFalse(WindowsSystemThemeDetector.parseIsDarkMode(null));
    }

    @Test
    @DisplayName("Should fall back to light mode for malformed/empty output")
    void shouldFallBackToLightModeForMalformedOutput() {
        assertFalse(WindowsSystemThemeDetector.parseIsDarkMode(""));
        assertFalse(WindowsSystemThemeDetector.parseIsDarkMode("garbage, not a reg query output at all"));
    }

    @Test
    @DisplayName("Should parse tab-separated output with a single line and no key header")
    void shouldParseTabSeparatedOutputWithoutKeyHeader() {
        String regQueryOutput = "AppsUseLightTheme\tREG_DWORD\t0x0";

        assertTrue(WindowsSystemThemeDetector.parseIsDarkMode(regQueryOutput));
    }
}
