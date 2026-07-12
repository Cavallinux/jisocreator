package cl.cavallinux.jisocreator.model.comparators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;

@DisplayName("ITreeNodeDirectoriesFirstComparator tests")
class ITreeNodeDirectoriesFirstComparatorTest {
    private final ITreeNodeDirectoriesFirstComparator comparator = new ITreeNodeDirectoriesFirstComparator();

    @Test
    @DisplayName("Should always categorize root node as first")
    void shouldAlwaysCategorizeRootNodeAsFirst() {
        StubTreeNode root = new StubTreeNode(true, null);

        assertEquals(0, comparator.category(root));
    }

    @Test
    @DisplayName("Should categorize non-root directory before non-root file")
    void shouldCategorizeNonRootDirectoryBeforeNonRootFile(@TempDir Path tempDir) throws IOException {
        Path directory = Files.createDirectory(tempDir.resolve("folder"));
        Path file = Files.write(tempDir.resolve("file.txt"), new byte[] { 1 });

        StubTreeNode directoryNode = new StubTreeNode(false, directory.toFile());
        StubTreeNode fileNode = new StubTreeNode(false, file.toFile());

        assertEquals(0, comparator.category(directoryNode));
        assertEquals(1, comparator.category(fileNode));
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
