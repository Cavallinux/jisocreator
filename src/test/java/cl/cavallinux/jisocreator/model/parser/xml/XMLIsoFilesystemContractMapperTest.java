package cl.cavallinux.jisocreator.model.parser.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;

@DisplayName("XMLIsoFilesystemContractMapper tests")
class XMLIsoFilesystemContractMapperTest {
    @Test
    @DisplayName("Should map IsoFileSystem metadata and entries to XML contract document")
    void shouldMapIsoFilesystemMetadataAndEntriesToXmlContractDocument(@TempDir Path tempDir) throws IOException {
        Path filePath = Files.write(tempDir.resolve("readme.txt"), new byte[] { 1, 2, 3 });
        StubTreeNode root = new StubTreeNode("/", "/", true, null);
        StubTreeNode child = new StubTreeNode("/readme.txt", filePath.toString(), false, filePath.toFile());
        root.addChild(child);
        IsoFileSystem iso = new IsoFileSystem(null, root, "VOL-123", "APP-123", "PUB-123", 3L);

        XMLIsoFilesystemContract.Iso9660Document document = XMLIsoFilesystemContractMapper.toDocument(iso);

        assertNotNull(document);
        assertEquals("VOL-123", document.getVolumeID());
        assertEquals("APP-123", document.getApplicationID());
        assertEquals("PUB-123", document.getPublisherID());
        assertEquals(3L, document.getIsoLength());
        assertNotNull(document.getRootEntry());
        assertTrue(document.getRootEntry().isRoot());
        assertEquals("/", document.getRootEntry().getIsoName());
        assertEquals("entry", document.getRootEntry().getEntryClass());
        assertEquals(1, document.getRootEntry().getChildren().size());
        XMLIsoFilesystemContract.Entry childEntry = document.getRootEntry().getChildren().get(0);
        assertEquals(filePath.toString(), childEntry.getFile());
        assertEquals("/readme.txt", childEntry.getIsoName());
        assertNotNull(childEntry.getParent());
        assertEquals("entry", childEntry.getParent().getEntryClass());
        assertEquals("../../..", childEntry.getParent().getReference());
    }

    @Test
    @DisplayName("Should map XML contract document back to IsoFileSystem tree")
    void shouldMapXmlContractDocumentBackToIsoFilesystemTree(@TempDir Path tempDir) throws IOException {
        Path filePath = Files.write(tempDir.resolve("manual.txt"), new byte[] { 9, 8 });
        XMLIsoFilesystemContract.Entry rootEntry = new XMLIsoFilesystemContract.Entry();
        rootEntry.setRoot(true);
        rootEntry.setIsoName("/");
        rootEntry.setEntryClass("entry");

        XMLIsoFilesystemContract.Entry childEntry = new XMLIsoFilesystemContract.Entry();
        childEntry.setRoot(false);
        childEntry.setIsoName("/manual.txt");
        childEntry.setFile(filePath.toString());

        XMLIsoFilesystemContract.ParentRef parentRef = new XMLIsoFilesystemContract.ParentRef();
        parentRef.setEntryClass("entry");
        parentRef.setReference("../../..");
        childEntry.setParent(parentRef);

        rootEntry.setChildren(List.of(childEntry));

        XMLIsoFilesystemContract.Iso9660Document document = new XMLIsoFilesystemContract.Iso9660Document();
        document.setVolumeID("VOL-777");
        document.setApplicationID("APP-777");
        document.setPublisherID("PUB-777");
        document.setIsoLength(2L);
        document.setRootEntry(rootEntry);

        IsoFileSystem iso = XMLIsoFilesystemContractMapper.toIsoFilesystem(document);

        assertNotNull(iso);
        assertEquals("VOL-777", iso.getVolumeID());
        assertEquals("APP-777", iso.getApplicationID());
        assertEquals("PUB-777", iso.getPublisherID());
        assertEquals(2L, iso.getIsoLength());
        assertNotNull(iso.getRoot());
        assertTrue(iso.getRoot().isRoot());
        assertEquals(1, iso.getRoot().getChildren().size());
        ITreeNode mappedChild = iso.getRoot().getChildren().get(0);
        assertSame(iso.getRoot(), mappedChild.getParent());
        assertEquals("/manual.txt", mappedChild.getIsoName());
        assertEquals(filePath.toFile().getAbsolutePath(), mappedChild.getExtendedName());
    }

    @Test
    @DisplayName("Should return null when source document is null or has no root entry")
    void shouldReturnNullWhenSourceDocumentIsNullOrHasNoRootEntry() {
        assertNull(XMLIsoFilesystemContractMapper.toIsoFilesystem(null));

        XMLIsoFilesystemContract.Iso9660Document document = new XMLIsoFilesystemContract.Iso9660Document();
        assertNull(XMLIsoFilesystemContractMapper.toIsoFilesystem(document));
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
