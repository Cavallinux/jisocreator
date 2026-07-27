package cl.cavallinux.jisocreator.testsupport;

import java.nio.file.Path;

import org.eclipse.swt.SWT;
import org.junit.jupiter.api.Assumptions;

/**
 * Ayuda compartida para tests que ejercitan APIs de SWT respaldadas por
 * bibliotecas nativas (p.e. {@code Transfer} de DnD, {@code Display}).
 *
 * <p>El proyecto define profiles Maven por plataforma ({@code linux},
 * {@code windows}, {@code applesilicon}, ...) que seleccionan en tiempo de
 * build el fragmento nativo de SWT a incluir en el classpath
 * (gtk/win32/cocoa). {@link SWT#getPlatform()} es una constante pura que
 * refleja ese fragmento sin disparar ninguna carga nativa por si misma, lo
 * que permite detectar de forma segura, ANTES de invocar cualquier API
 * nativa real, si el fragmento activo coincide con el sistema operativo del
 * host de ejecucion.
 *
 * <p>Cuando el profile activo no coincide con el host (p.e. compilando con
 * {@code -Pwindows} sobre un host Linux/macOS para simular otra plataforma),
 * invocar directamente una API nativa de SWT (como
 * {@code FileTransfer.getInstance()} o {@code Display.getDefault()}) hace que
 * SWT invoque un {@code System.exit(1)} interno no capturable, colapsando la
 * JVM del test en lugar de lanzar una excepcion Java. Por ello, los tests que
 * dependen de dichas APIs deben llamar a
 * {@link #assumeNativePlatformMatches()} como primera linea antes de tocar
 * cualquier API nativa, de forma que se omitan (skip) de forma fluida bajo un
 * profile incompatible en vez de abortar la JVM completa.
 */
public final class SwtPlatformAssumptions {

    private SwtPlatformAssumptions() {
    }

    /**
     * Realiza un {@link Assumptions#assumeTrue(boolean, java.util.function.Supplier)}
     * que omite el test actual cuando el fragmento nativo de SWT presente en
     * el classpath (segun el profile Maven activo) no coincide con el
     * sistema operativo real del host de ejecucion.
     */
    public static void assumeNativePlatformMatches() {
        String swtPlatform = SWT.getPlatform();
        String osName = System.getProperty("os.name", "").toLowerCase();
        boolean matches = switch (swtPlatform) {
        case "gtk" -> osName.contains("nux") || osName.contains("nix");
        case "win32" -> osName.contains("win");
        case "cocoa" -> osName.contains("mac");
        default -> false;
        };

        Assumptions.assumeTrue(matches,
                () -> String.format(
                        "Omitiendo test: el fragmento nativo de SWT activo ('%s') no coincide con el host actual "
                                + "('%s'). Esto ocurre al activar un profile Maven de otra plataforma (p.e. "
                                + "-Pwindows sobre un host Unix); invocar la API nativa aqui haria colapsar la JVM.",
                        swtPlatform, osName));
    }

    /**
     * Omite el test actual cuando el sistema de archivos subyacente al {@code path}
     * indicado no soporta permisos POSIX (p.ej. NTFS en Windows).
     *
     * <p>Debe invocarse antes de cualquier llamada a
     * {@link java.nio.file.Files#setPosixFilePermissions} para evitar un
     * {@link UnsupportedOperationException} en sistemas no POSIX.
     *
     * @param path ruta cuyo sistema de archivos se comprobara
     */
    public static void assumePosixPermissionsSupported(Path path) {
        Assumptions.assumeTrue(
                path.getFileSystem().supportedFileAttributeViews().contains("posix"),
                "Omitiendo test: el sistema de archivos no soporta permisos POSIX "
                        + "(p.ej. NTFS en Windows).");
    }
}
