package cl.cavallinux.jisocreator.model.filters.isoexplorer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;

@DisplayName("ShowOnlyIsoDirectoriesFilter tests")
class ShowOnlyIsoDirectoriesFilterTest {
    private final ShowOnlyIsoDirectoriesFilter filter = new ShowOnlyIsoDirectoriesFilter();

    @Test
    @DisplayName("Should select root node regardless of element file")
    void shouldSelectRootNodeRegardlessOfElementFile() {
        StubTreeNode root = new StubTreeNode(true, null);

        assertTrue(filter.select(null, null, root));
    }

    @Test
    @DisplayName("Should select non-root directory nodes and reject files")
    void shouldSelectNonRootDirectoryNodesAndRejectFiles(@TempDir Path tempDir) throws IOException {
        Path dir = Files.createDirectory(tempDir.resolve("dir"));
        Path file = Files.write(tempDir.resolve("file.txt"), new byte[] { 1 });

        assertTrue(filter.select(null, null, new StubTreeNode(false, dir.toFile())));
        assertFalse(filter.select(null, null, new StubTreeNode(false, file.toFile())));
    }

    private record StubTreeNode(boolean root, File element) implements ITreeNode {
        @Override
        public boolean isRoot() {
            return root;
        }

        @Override
        public Object getElement() {
            return element;
        }
    }
}
