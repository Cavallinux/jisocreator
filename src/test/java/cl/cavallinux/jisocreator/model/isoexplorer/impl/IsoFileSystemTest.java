package cl.cavallinux.jisocreator.model.isoexplorer.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;

@DisplayName("IsoFileSystem tests")
class IsoFileSystemTest {
    @Test
    @DisplayName("Should parse all leaf nodes into isoPaths")
    void shouldParseAllLeafNodesIntoIsoPaths() {
        StubTreeNode root = new StubTreeNode("/", "/", true, null);
        StubTreeNode directory = new StubTreeNode("/docs/", "/tmp/docs", false, null);
        StubTreeNode fileA = new StubTreeNode("/docs/readme.txt", "/tmp/docs/readme.txt", false, null);
        StubTreeNode fileB = new StubTreeNode("/docs/manual.pdf", "/tmp/docs/manual.pdf", false, null);
        root.addChild(directory);
        directory.addChild(fileA);
        directory.addChild(fileB);

        IsoFileSystem iso = new IsoFileSystem(null, root, "VOL", "APP", "PUB", 0L);
        iso.parse();

        assertEquals(List.of("/docs/readme.txt=/tmp/docs/readme.txt", "/docs/manual.pdf=/tmp/docs/manual.pdf"),
                iso.getIsoPaths());
    }

    @Test
    @DisplayName("Should calculate isoLength by summing leaf file sizes")
    void shouldCalculateIsoLengthBySummingLeafFileSizes(@TempDir Path tempDir) throws IOException {
        Path fileA = Files.write(tempDir.resolve("a.bin"), new byte[] { 1, 2, 3 });
        Path fileB = Files.write(tempDir.resolve("b.bin"), new byte[] { 9, 8 });
        StubTreeNode root = new StubTreeNode("/", "/", true, null);
        StubTreeNode nodeA = new StubTreeNode("/a.bin", fileA.toString(), false, fileA.toFile());
        StubTreeNode nodeB = new StubTreeNode("/b.bin", fileB.toString(), false, fileB.toFile());
        root.addChild(nodeA);
        root.addChild(nodeB);

        IsoFileSystem iso = new IsoFileSystem(null, root, "VOL", "APP", "PUB", 0L);
        iso.setIsoLength();

        assertEquals(5L, iso.getIsoLength());
    }

    @Test
    @DisplayName("Should replace volume and length placeholders in info template")
    void shouldReplaceVolumeAndLengthPlaceholdersInInfoTemplate() {
        IsoFileSystem iso = new IsoFileSystem(null, new StubTreeNode("/", "/", true, null), "VOL-999", "APP", "PUB",
                42L);

        String info = iso.printIsoFileSystemInfo("Volume=<volume_id>; Length=<length>");

        assertEquals("Volume=VOL-999; Length=42", info);
    }

    @Test
    @DisplayName("Should expose root as the only top-level element in toArray")
    void shouldExposeRootAsTheOnlyTopLevelElementInToArray() {
        StubTreeNode root = new StubTreeNode("/", "/", true, null);
        IsoFileSystem iso = new IsoFileSystem(null, root, "VOL", "APP", "PUB", 0L);

        Object[] array = iso.toArray();

        assertArrayEquals(new Object[] { root }, array);
    }

    private static final class StubTreeNode implements ITreeNode {
        private final String isoName;
        private final String extendedName;
        private final boolean root;
        private final File element;
        private final List<ITreeNode> children = new ArrayList<>();
        private ITreeNode parent;

        private StubTreeNode(String isoName, String extendedName, boolean root, File element) {
            this.isoName = isoName;
            this.extendedName = extendedName;
            this.root = root;
            this.element = element;
        }

        private void addChild(StubTreeNode child) {
            child.parent = this;
            children.add(child);
        }

        @Override
        public Object getElement() {
            return element;
        }

        @Override
        public String getIsoName() {
            return isoName;
        }

        @Override
        public String getExtendedName() {
            return extendedName;
        }

        @Override
        public ITreeNode getParent() {
            return parent;
        }

        @Override
        public List<ITreeNode> getChildren() {
            return children;
        }

        @Override
        public Object[] toArray() {
            return children.toArray();
        }

        @Override
        public boolean hasChildren() {
            return !children.isEmpty();
        }

        @Override
        public boolean isRoot() {
            return root;
        }
    }
}
