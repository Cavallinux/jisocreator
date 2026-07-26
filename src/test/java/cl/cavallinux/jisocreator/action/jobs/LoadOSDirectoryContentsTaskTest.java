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
 * Tests para {@link LoadOSDirectoryContentsTask}.
 *
 * <p>{@code submit()}/{@code run()} dependen de un {@code ExecutorService}
 * compartido en segundo plano, de {@code OSAndIsoExplorerManager.INSTANCE.getOsExplorer()}
 * y de {@code Display.getDefault()}/{@code TableViewer#setInput(Object)}, por lo
 * que no son invocables de forma headless sin riesgo de dejar recursos GUI
 * compartidos (el {@code Display} singleton) en un estado inconsistente entre
 * tests — el mismo tipo de exclusion aplicada a otros hilos/tareas del proyecto
 * (ver {@code ToggleHiddenFilesOSExplorerThreadTest}). El metodo package-private
 * {@code isStillLatestRequest()} es, en cambio, logica pura basada en un
 * {@code AtomicReference} estatico actualizado en el constructor, y se cubre
 * construyendo instancias via el builder sin invocar {@code submit()}/{@code run()}.
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
@DisplayName("LoadOSDirectoryContentsTask tests")
class LoadOSDirectoryContentsTaskTest {

    @Test
    @DisplayName("Builder should produce a Runnable instance")
    void builderShouldProduceRunnableInstance(@TempDir File tempDir) {
        LoadOSDirectoryContentsTask task = LoadOSDirectoryContentsTask.builder()
                .directory(tempDir)
                .tableViewer(null)
                .build();

        assertTrue(task instanceof Runnable);
    }

    @Test
    @DisplayName("isStillLatestRequest should return true for the most recently built task")
    void isStillLatestRequestShouldReturnTrueForMostRecentlyBuiltTask(@TempDir File tempDir) {
        LoadOSDirectoryContentsTask task = LoadOSDirectoryContentsTask.builder()
                .directory(tempDir)
                .tableViewer(null)
                .build();

        assertTrue(task.isStillLatestRequest());
    }

    @Test
    @DisplayName("isStillLatestRequest should return false once a newer directory request supersedes it")
    void isStillLatestRequestShouldReturnFalseWhenSupersededByNewerRequest(@TempDir File tempDir) throws Exception {
        File firstDirectory = new File(tempDir, "first");
        File secondDirectory = new File(tempDir, "second");
        firstDirectory.mkdir();
        secondDirectory.mkdir();

        LoadOSDirectoryContentsTask firstRequest = LoadOSDirectoryContentsTask.builder()
                .directory(firstDirectory)
                .tableViewer(null)
                .build();
        LoadOSDirectoryContentsTask secondRequest = LoadOSDirectoryContentsTask.builder()
                .directory(secondDirectory)
                .tableViewer(null)
                .build();

        assertFalse(firstRequest.isStillLatestRequest());
        assertTrue(secondRequest.isStillLatestRequest());
    }

    @Test
    @DisplayName("showBusyCursor/restoreDefaultCursor should be safe no-ops without a real TableViewer")
    void busyCursorMethodsShouldBeNoOpsWithoutTableViewer(@TempDir File tempDir) throws Exception {
        LoadOSDirectoryContentsTask task = LoadOSDirectoryContentsTask.builder()
                .directory(tempDir)
                .tableViewer(null)
                .build();

        assertDoesNotThrow(() -> invokePrivateNoArgMethod(task, "showBusyCursor"));
        assertDoesNotThrow(() -> invokePrivateNoArgMethod(task, "restoreDefaultCursor"));
    }

    private static void invokePrivateNoArgMethod(LoadOSDirectoryContentsTask target, String methodName)
            throws Exception {
        Method method = LoadOSDirectoryContentsTask.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }
}

