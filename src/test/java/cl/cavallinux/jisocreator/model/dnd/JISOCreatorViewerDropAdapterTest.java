package cl.cavallinux.jisocreator.model.dnd;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.testsupport.SwtPlatformAssumptions;
import cl.cavallinux.jisocreator.testsupport.TransferDataTestSupport;

/**
 * Tests para {@link JISOCreatorViewerDropAdapter}.
 *
 * <p><b>{@code performDrop} no es headless-testable de forma portable:</b>
 * internamente invoca incondicionalmente
 * {@code OSExplorerActionsManager.ADDFILEACTION.getAction()}, cuyo
 * inicializador estatico de enum construye TODAS las acciones del explorador
 * OS de forma ansiosa, incluyendo llamadas a
 * {@code ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor(...)},
 * que a su vez ejecutan {@code Display.getDefault()} y fuerzan la carga de
 * las bibliotecas nativas de SWT del sistema. Ante un mismatch de profile
 * (p.e. {@code -Pwindows} sobre un host Unix) SWT invoca un
 * {@code System.exit(1)} interno no capturable dentro de esa cadena, sin
 * forma de que un {@code try/catch} en {@code performDrop} lo intercepte. Por
 * esta razon no se ejercita {@code performDrop} en este test unitario.
 *
 * <p>{@code validateDrop} si se cubre, pero depende de
 * {@code FileTransfer.getInstance()} (registro nativo de tipos DnD), que
 * tambien requiere que el fragmento nativo de SWT en el classpath coincida
 * con el sistema operativo real del host (ver
 * {@link SwtPlatformAssumptions}). Por eso cada test llama primero a
 * {@link SwtPlatformAssumptions#assumeNativePlatformMatches()}: si el profile
 * Maven activo no coincide con el host, el test se omite (skip) de forma
 * fluida en vez de intentar invocar la API nativa y colapsar la JVM.</p>
 */
@DisplayName("JISOCreatorViewerDropAdapter tests")
class JISOCreatorViewerDropAdapterTest {

    private JISOCreatorViewerDropAdapter adapter;

    @BeforeEach
    void setUp() {
        /** Los tests de esta clase ejercitan FileTransfer, respaldado por bibliotecas nativas de SWT. */
        SwtPlatformAssumptions.assumeNativePlatformMatches();
        adapter = JISOCreatorViewerDropAdapter.builder().viewer(null).build();
    }

    @Test
    @DisplayName("validateDrop should accept a FileTransfer-compatible TransferData")
    void validateDropShouldAcceptFileTransferType() throws Exception {
        TransferData transferData = TransferDataTestSupport.supportedTransferData(FileTransfer.getInstance());

        assertTrue(adapter.validateDrop(null, 0, transferData));
    }

    @Test
    @DisplayName("validateDrop should reject an unsupported TransferData type")
    void validateDropShouldRejectUnsupportedType() throws Exception {
        TransferData transferData = TransferDataTestSupport.unsupportedTransferData(FileTransfer.getInstance());

        assertFalse(adapter.validateDrop(null, 0, transferData));
    }
}
