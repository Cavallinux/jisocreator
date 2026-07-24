package cl.cavallinux.jisocreator.model.providers.isoexplorer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoTreeNode;

/**
 * Tests para {@link IsoTreeLabelProvider}, análogos a los ya existentes para
 * {@code OSTreeLabelProviderTest}.
 */
@DisplayName("IsoTreeLabelProvider tests")
class IsoTreeLabelProviderTest {
    private final IsoTreeLabelProvider provider = new IsoTreeLabelProvider();

    @Test
    @DisplayName("Should use the ISO root name for root nodes")
    void shouldUseIsoRootNameForRootNodes() {
        IsoTreeNode root = new IsoTreeNode();

        assertEquals("/", provider.getText(root));
    }

    @Test
    @DisplayName("Should use the short (file) name for non-root nodes")
    void shouldUseShortNameForNonRootNodes(@TempDir Path tempDir) throws IOException {
        IsoTreeNode root = new IsoTreeNode();
        Path file = Files.write(tempDir.resolve("example.txt"), new byte[] { 1 });
        IsoTreeNode child = new IsoTreeNode(root, file.toFile(), false);

        assertEquals("example.txt", provider.getText(child));
    }
}
