package cl.cavallinux.jisocreator.model.parser.decl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;

@DisplayName("IsoFilesystemParser default behavior tests")
class IsoFilesystemParserTest {
    private final IsoFilesystemParser<IsoFileSystem> parser = new IsoFilesystemParser<>() {
    };

    @Test
    @DisplayName("Should expose safe default serialization behavior")
    void shouldExposeSafeDefaultSerializationBehavior() {
        assertTrue(parser.deserialize("any-path.xml").isEmpty());
        assertFalse(parser.serialize(new IsoFileSystem(), "any-path.xml"));
    }

    @Test
    @DisplayName("Should generate valid initial volume id")
    void shouldGenerateValidInitialVolumeId() {
        String volumeId = parser.generateInitialVolumeID();

        assertTrue(volumeId.length() <= IsoFilesystemParser.MKISOFS_VOLUMEID_MAXLENGTH);
        assertFalse(volumeId.contains("-"));
        assertFalse(StringUtils.isBlank(volumeId));
    }

    @Test
    @DisplayName("Should resolve application id from manifest or fallback")
    void shouldResolveApplicationIdFromManifestOrFallback() {
        String applicationId = parser.generateIsoFilesystemApplicationID();

        if (StringUtils.isBlank(IsoFilesystemParser.MKISOFS_ISOFILESYSTEM_APPLICATIONID)) {
            assertEquals(IsoFilesystemParser.DEFAULT_ISOFILESYSTEM_APPLICATIONID, applicationId);
        } else {
            assertEquals(IsoFilesystemParser.MKISOFS_ISOFILESYSTEM_APPLICATIONID, applicationId);
        }
    }
}
