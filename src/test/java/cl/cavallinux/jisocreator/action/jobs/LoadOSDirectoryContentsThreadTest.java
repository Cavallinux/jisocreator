package cl.cavallinux.jisocreator.action.jobs;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.lang.reflect.Method;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests para {@link LoadOSDirectoryContentsThread}.
 *
 * <p>{@code run()} depende de {@code OSAndIsoExplorerManager.INSTANCE.getOsExplorer()}
 * y de {@code Display.getDefault()}/{@code TableViewer#setInput(Object)}, por lo
 * que no es invocable de forma headless — el mismo tipo de exclusion aplicada a
 * otros hilos del proyecto (ver {@code ToggleHiddenFilesOSExplorerThreadTest}).
 * El metodo package-private {@code isStillLatestRequest()} es, en cambio, logica
 * pura basada en un {@code AtomicReference} estatico actualizado en el
 * constructor, y se cubre construyendo instancias via el builder sin invocar
 * {@code start()}/{@code run()}.
 *
 * <p>Los metodos privados {@code showBusyCursor()}/{@code restoreDefaultCursor()}
 * (feedback visual de cursor ocupado durante la carga en background) requieren un
 * {@code Shell}/{@code Display} real para verificar el cambio de cursor en si, por
 * lo que ese comportamiento visual no es testeable headlessly (misma exclusion que
 * el resto del layer GUI-acoplado). Lo que si se cubre aqui es su guarda de
 * nulidad: ambos metodos deben ser no-ops seguros cuando {@code tableViewer} es
 * {@code null} (como ocurre en el resto de los tests de esta clase, que no
 * disponen de un {@code TableViewer} real), invocandolos por reflexion.
 */
@DisplayName("LoadOSDirectoryContentsThread tests")
class LoadOSDirectoryContentsThreadTest {

    @Test
    @DisplayName("Builder should produce a Thread instance")
    void builderShouldProduceThreadInstance(@TempDir File tempDir) {
        LoadOSDirectoryContentsThread thread = LoadOSDirectoryContentsThread.builder()
                .directory(tempDir)
                .tableViewer(null)
                .build();

        assertTrue(thread instanceof Thread);
    }

    @Test
    @DisplayName("isStillLatestRequest should return true for the most recently built thread")
    void isStillLatestRequestShouldReturnTrueForMostRecentlyBuiltThread(@TempDir File tempDir) {
        LoadOSDirectoryContentsThread thread = LoadOSDirectoryContentsThread.builder()
                .directory(tempDir)
                .tableViewer(null)
                .build();

        assertTrue(thread.isStillLatestRequest());
    }

    @Test
    @DisplayName("isStillLatestRequest should return false once a newer directory request supersedes it")
    void isStillLatestRequestShouldReturnFalseWhenSupersededByNewerRequest(@TempDir File tempDir) throws Exception {
        File firstDirectory = new File(tempDir, "first");
        File secondDirectory = new File(tempDir, "second");
        firstDirectory.mkdir();
        secondDirectory.mkdir();

        LoadOSDirectoryContentsThread firstRequest = LoadOSDirectoryContentsThread.builder()
                .directory(firstDirectory)
                .tableViewer(null)
                .build();
        LoadOSDirectoryContentsThread secondRequest = LoadOSDirectoryContentsThread.builder()
                .directory(secondDirectory)
                .tableViewer(null)
                .build();

        assertFalse(firstRequest.isStillLatestRequest());
        assertTrue(secondRequest.isStillLatestRequest());
    }

    @Test
    @DisplayName("showBusyCursor/restoreDefaultCursor should be safe no-ops without a real TableViewer")
    void busyCursorMethodsShouldBeNoOpsWithoutTableViewer(@TempDir File tempDir) throws Exception {
        LoadOSDirectoryContentsThread thread = LoadOSDirectoryContentsThread.builder()
                .directory(tempDir)
                .tableViewer(null)
                .build();

        assertDoesNotThrow(() -> invokePrivateNoArgMethod(thread, "showBusyCursor"));
        assertDoesNotThrow(() -> invokePrivateNoArgMethod(thread, "restoreDefaultCursor"));
    }

    private static void invokePrivateNoArgMethod(LoadOSDirectoryContentsThread target, String methodName)
            throws Exception {
        Method method = LoadOSDirectoryContentsThread.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }
}

