package cl.cavallinux.jisocreator.model.parser.xml;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;

@DisplayName("XMLIsoFilesystemParser tests")
class XMLIsoFilesystemParserTest {
    @Test
    @DisplayName("Should return empty when XML path does not exist")
    void shouldReturnEmptyWhenXmlPathDoesNotExist() {
        XMLIsoFilesystemParser parser = XMLIsoFilesystemParser.builder().build();

        Optional<IsoFileSystem> result = parser.deserialize("/path/that/does/not/exist.xml");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty when XML content is invalid")
    void shouldReturnEmptyWhenXmlContentIsInvalid(@TempDir Path tempDir) throws IOException {
        XMLIsoFilesystemParser parser = XMLIsoFilesystemParser.builder().build();
        Path invalidXml = tempDir.resolve("invalid.xml");
        Files.writeString(invalidXml, "<iso9660><RootEntry>", StandardCharsets.UTF_8);

        Optional<IsoFileSystem> result = parser.deserialize(invalidXml.toString());

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should repair blank application and publisher IDs after deserialization")
    void shouldRepairBlankApplicationAndPublisherIdsAfterDeserialization(@TempDir Path tempDir) throws IOException {
        XMLIsoFilesystemParser parser = XMLIsoFilesystemParser.builder().build();
        Path xml = tempDir.resolve("layout.xml");
        Files.writeString(xml, """
                <iso9660 volumeid="VOL-001" applicationid="" publisherid="" isolength="123">
                  <RootEntry class="entry" isoname="/" root="true">
                    <children/>
                  </RootEntry>
                </iso9660>
                """, StandardCharsets.UTF_8);

        Optional<IsoFileSystem> result = parser.deserialize(xml.toString());

        assertTrue(result.isPresent());
        IsoFileSystem iso = result.get();
        assertEquals("VOL-001", iso.getVolumeID());
        assertEquals(123L, iso.getIsoLength());
        assertEquals(parser.generateIsoFilesystemApplicationID(), iso.getApplicationID());
        assertTrue(StringUtils.isNotBlank(iso.getPublisherID()));
        assertDoesNotThrow(() -> UUID.fromString(iso.getPublisherID()));
        assertNotNull(iso.getRoot());
        assertTrue(iso.getRoot().isRoot());
        assertFalse(iso.getRoot().hasChildren());
    }

    @Test
    @DisplayName("Should return false when serialize target is not writable")
    void shouldReturnFalseWhenSerializeTargetIsNotWritable(@TempDir Path tempDir) {
        XMLIsoFilesystemParser parser = XMLIsoFilesystemParser.builder().build();
        IsoFileSystem iso = new IsoFileSystem();
        Path outputDir = tempDir.resolve("output-dir");
        assertDoesNotThrow(() -> Files.createDirectories(outputDir));

        boolean serialized = parser.serialize(iso, outputDir.toString());

        assertFalse(serialized);
    }
}
