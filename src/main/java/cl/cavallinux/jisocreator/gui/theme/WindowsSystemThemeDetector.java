package cl.cavallinux.jisocreator.gui.theme;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

/**
 * Detecta si el sistema operativo Windows tiene activo el modo oscuro,
 * consultando el valor {@code AppsUseLightTheme} del registro de Windows.
 *
 * <p>Esta clase deliberadamente no depende de SWT/{@code Display}: invoca
 * {@code reg.exe} vía {@link ProcessBuilder} y parsea su salida como texto
 * plano. Esto permite testear la logica de parseo ({@link #parseIsDarkMode})
 * de forma aislada y portable (sin Display, sin reg.exe real, sin depender
 * del sistema operativo del host de test), consistente con la convencion del
 * proyecto de no crear un {@code Display} real en tests unitarios.</p>
 */
@Slf4j
public final class WindowsSystemThemeDetector {
    private static final String REGISTRY_KEY_PATH =
            "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize";
    private static final String REGISTRY_VALUE_NAME = "AppsUseLightTheme";
    private static final Pattern APPS_USE_LIGHT_THEME_PATTERN = Pattern
            .compile("AppsUseLightTheme\\s+REG_DWORD\\s+0x([0-9a-fA-F]+)");
    private static final long REG_QUERY_TIMEOUT_SECONDS = 2L;

    private WindowsSystemThemeDetector() {
    }

    /**
     * Consulta el registro de Windows para determinar si el modo oscuro del
     * sistema esta activo.
     *
     * @return {@code true} si {@code AppsUseLightTheme} es 0 (modo oscuro); {@code false}
     * si es 1 (modo claro) o si la consulta falla por cualquier motivo (fallback seguro
     * a claro, para no forzar oscuro de forma inesperada ante un error de deteccion).
     */
    public static boolean isSystemDarkMode() {
        try {
            Process process = new ProcessBuilder("reg", "query", REGISTRY_KEY_PATH, "/v", REGISTRY_VALUE_NAME)
                    .redirectErrorStream(true).start();
            String output;
            try (InputStream processOutput = process.getInputStream()) {
                output = new String(processOutput.readAllBytes(), StandardCharsets.UTF_8);
            }
            process.waitFor(REG_QUERY_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            return parseIsDarkMode(output);
        } catch (IOException e) {
            log.warn("Error querying Windows registry for system theme; assuming light mode", e);
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted while querying Windows registry for system theme; assuming light mode", e);
            return false;
        }
    }

    /**
     * Parses the raw stdout of {@code reg query ... /v AppsUseLightTheme} and determines
     * whether it indicates dark mode. Package-private and static so it can be unit tested
     * with canned input, independent of any real registry access or Windows host.
     *
     * @param regQueryOutput raw output text (may be {@code null} or malformed)
     * @return {@code true} only when the parsed {@code AppsUseLightTheme} DWORD is exactly 0
     */
    static boolean parseIsDarkMode(String regQueryOutput) {
        if (Objects.isNull(regQueryOutput)) {
            return false;
        }

        Matcher matcher = APPS_USE_LIGHT_THEME_PATTERN.matcher(regQueryOutput);
        if (!matcher.find()) {
            return false;
        }

        try {
            return Integer.parseInt(matcher.group(1), 16) == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
