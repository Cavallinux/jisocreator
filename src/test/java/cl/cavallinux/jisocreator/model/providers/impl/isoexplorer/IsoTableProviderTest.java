package cl.cavallinux.jisocreator.model.providers.impl.isoexplorer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.swt.graphics.Image;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.model.providers.isoexplorer.IsoTableProvider;

@DisplayName("IsoTableProvider tests")
class IsoTableProviderTest {
    private final IsoTableProvider provider = new IsoTableProvider();

    @Test
    @DisplayName("Should expose node children as table elements")
    void shouldExposeNodeChildrenAsTableElements() {
        ITreeNode childA = new StubIsoNode(null, null, null);
        ITreeNode childB = new StubIsoNode(null, null, null);
        ITreeNode parent = new StubIsoNode(null, new Object[] { childA, childB }, null);

        assertArrayEquals(new Object[] { childA, childB }, provider.getElements(parent));
    }

    @Test
    @DisplayName("Should provide image in first column only")
    void shouldProvideImageInFirstColumnOnly() {
        StubIsoNode node = new StubIsoNode(null, new Object[0], null);

        assertNull(provider.getColumnImage(node, 0));
        assertNull(provider.getColumnImage(node, 1));
    }

    @Test
    @DisplayName("Should provide expected text values for file columns")
    void shouldProvideExpectedTextValuesForFileColumns(@TempDir Path tempDir) throws IOException {
        Path file = Files.write(tempDir.resolve("sample"), new byte[] { 1, 2, 3 });
        StubIsoNode node = new StubIsoNode(file.toFile(), new Object[0], null);

        assertEquals("sample", provider.getColumnText(node, 0));
        assertEquals("File", provider.getColumnText(node, 1));
        assertEquals("3", provider.getColumnText(node, 2));
        assertEquals("", provider.getColumnText(node, 99));
    }

    private record StubIsoNode(File file, Object[] children, Image image) implements ITreeNode {
        @Override
        public Object[] toArray() {
            return children;
        }

        @Override
        public Object getElement() {
            return file;
        }

        @Override
        public Image getImage() {
            return image;
        }
    }
}
