package cl.cavallinux.jisocreator.instances;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;

@DisplayName("OSAndIsoExplorerManager tests")
class OSAndIsoExplorerManagerTest {
    @Test
    @DisplayName("Should expose singleton OSExplorer instance with roots initialized")
    void shouldExposeSingletonOsExplorerInstanceWithRootsInitialized() {
        OSExplorer explorer = OSAndIsoExplorerManager.INSTANCE.getOsExplorer();

        assertNotNull(explorer);
        assertSame(explorer, OSAndIsoExplorerManager.INSTANCE.getOsExplorer());
        assertNotNull(explorer.getRootPaths());
        assertTrue(explorer.getRootPaths().length > 0);
    }
}
