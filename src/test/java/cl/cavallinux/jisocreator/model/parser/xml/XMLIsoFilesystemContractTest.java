package cl.cavallinux.jisocreator.model.parser.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("XMLIsoFilesystemContract DTO tests")
class XMLIsoFilesystemContractTest {
    @Test
    @DisplayName("Should keep document metadata and root entry values")
    void shouldKeepDocumentMetadataAndRootEntryValues() {
        XMLIsoFilesystemContract.Entry root = new XMLIsoFilesystemContract.Entry();
        root.setRoot(true);
        root.setIsoName("/");
        root.setEntryClass("entry");

        XMLIsoFilesystemContract.Iso9660Document document = new XMLIsoFilesystemContract.Iso9660Document();
        document.setVolumeID("VOL-A");
        document.setApplicationID("APP-A");
        document.setPublisherID("PUB-A");
        document.setIsoLength(10L);
        document.setRootEntry(root);

        assertEquals("VOL-A", document.getVolumeID());
        assertEquals("APP-A", document.getApplicationID());
        assertEquals("PUB-A", document.getPublisherID());
        assertEquals(10L, document.getIsoLength());
        assertTrue(document.getRootEntry().isRoot());
        assertEquals("/", document.getRootEntry().getIsoName());
    }

    @Test
    @DisplayName("Should initialize and keep entry children and parent references")
    void shouldInitializeAndKeepEntryChildrenAndParentReferences() {
        XMLIsoFilesystemContract.Entry parent = new XMLIsoFilesystemContract.Entry();
        XMLIsoFilesystemContract.Entry child = new XMLIsoFilesystemContract.Entry();
        child.setIsoName("/child.txt");

        XMLIsoFilesystemContract.ParentRef parentRef = new XMLIsoFilesystemContract.ParentRef();
        parentRef.setEntryClass("entry");
        parentRef.setReference("../../..");
        child.setParent(parentRef);

        assertNotNull(parent.getChildren());
        assertTrue(parent.getChildren().isEmpty());
        parent.setChildren(List.of(child));

        assertFalse(parent.getChildren().isEmpty());
        assertEquals("/child.txt", parent.getChildren().get(0).getIsoName());
        assertEquals("entry", parent.getChildren().get(0).getParent().getEntryClass());
        assertEquals("../../..", parent.getChildren().get(0).getParent().getReference());
    }
}
