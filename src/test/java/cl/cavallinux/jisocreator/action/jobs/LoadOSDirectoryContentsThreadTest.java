package cl.cavallinux.jisocreator.action.jobs;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

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
}
