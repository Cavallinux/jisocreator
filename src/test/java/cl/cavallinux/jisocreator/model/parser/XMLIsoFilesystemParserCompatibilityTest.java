package cl.cavallinux.jisocreator.model.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;

@DisplayName("XMLIsoFilesystemParser compatibility tests")
class XMLIsoFilesystemParserCompatibilityTest {
    private static final String LEGACY_SAMPLE_XML = "xml/c267b1a84ea9429088ce5530122e5c8a.xml";

    @Test
    @DisplayName("Should deserialize existing legacy XML layout")
    void shouldDeserializeLegacyXmlLayout() {
        XMLIsoFilesystemParser parser = XMLIsoFilesystemParser.builder().build();

        Optional<IsoFileSystem> deserialized = parser.deserialize(LEGACY_SAMPLE_XML);

        assertTrue(deserialized.isPresent());
        IsoFileSystem isoFileSystem = deserialized.get();
        assertNotNull(isoFileSystem.getRoot());
        assertTrue(isoFileSystem.getRoot().isRoot());
        assertEquals("/", isoFileSystem.getRoot().getIsoName());
        assertTrue(isoFileSystem.getRoot().hasChildren());
        assertEquals("c267b1a84ea9429088ce5530122e5c8a", isoFileSystem.getVolumeID());
    }

    @Test
    @DisplayName("Should keep XML contract compatible after deserialize and serialize")
    void shouldKeepXmlContractCompatibleAfterRoundTrip(@TempDir Path tempDir) throws IOException {
        XMLIsoFilesystemParser parser = XMLIsoFilesystemParser.builder().build();
        Optional<IsoFileSystem> deserialized = parser.deserialize(LEGACY_SAMPLE_XML);
        assertTrue(deserialized.isPresent());

        Path outputXml = tempDir.resolve("layout.xml");
        boolean serialized = parser.serialize(deserialized.get(), outputXml.toString());

        assertTrue(serialized);
        String expectedXml = Files.readString(Path.of(LEGACY_SAMPLE_XML), StandardCharsets.UTF_8);
        String actualXml = Files.readString(outputXml, StandardCharsets.UTF_8);

        Diff diff = DiffBuilder.compare(expectedXml).withTest(actualXml).ignoreWhitespace().checkForSimilar().build();
        assertFalse(diff.hasDifferences(), () -> "XML compatibility diff: " + diff);
    }
}
