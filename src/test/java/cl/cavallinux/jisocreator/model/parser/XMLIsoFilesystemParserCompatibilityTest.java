package cl.cavallinux.jisocreator.model.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.lang3.Strings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.model.parser.xml.XMLIsoFilesystemParser;

@DisplayName("XMLIsoFilesystemParser compatibility tests")
class XMLIsoFilesystemParserCompatibilityTest {
    private static final String LEGACY_SAMPLE_XML_CLASSPATH = "/xml/c267b1a84ea9429088ce5530122e5c8a.xml";
    private static final String LEGACY_WIN32_SAMPLE_XML_CLASSPATH = "/xml/1560506077724c8e97f4d1664f851193.xml";

    @Test
    @DisplayName("Should deserialize existing legacy XML layout")
    void shouldDeserializeLegacyXmlLayout() {
        XMLIsoFilesystemParser parser = XMLIsoFilesystemParser.builder().build();

        Path legacyXml = getLegacySampleXmlPath();
        Optional<IsoFileSystem> deserialized = parser.deserialize(legacyXml.toString());

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
        Path legacyXml = getLegacySampleXmlPath();
        Optional<IsoFileSystem> deserialized = parser.deserialize(legacyXml.toString());
        assertTrue(deserialized.isPresent());

        Path outputXml = tempDir.resolve("layout.xml");
        boolean serialized = parser.serialize(deserialized.get(), outputXml.toString());

        assertTrue(serialized);
        String expectedXml = Files.readString(legacyXml, StandardCharsets.UTF_8);
        String actualXml = Files.readString(outputXml, StandardCharsets.UTF_8);

        Diff diff = DiffBuilder.compare(expectedXml).withTest(actualXml).ignoreWhitespace().checkForSimilar().build();
        assertFalse(diff.hasDifferences(), () -> "XML compatibility diff: " + diff);
    }

    private static Path getLegacySampleXmlPath() {
        String xml = Strings.CI.containsAny(System.getProperty("os.name"), "Windows")
                ? LEGACY_WIN32_SAMPLE_XML_CLASSPATH
                : LEGACY_SAMPLE_XML_CLASSPATH;
        URL resource = XMLIsoFilesystemParserCompatibilityTest.class.getResource(xml);
        assertNotNull(resource, "Missing test resource: " + xml);
        try {
            return Path.of(resource.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid test resource URI: " + xml, e);
        }
    }
}
