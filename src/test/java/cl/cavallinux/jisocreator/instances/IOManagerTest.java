package cl.cavallinux.jisocreator.instances;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.model.parser.xml.XMLIsoFilesystemParser;
import cl.cavallinux.jisocreator.util.IOUtils;

@DisplayName("IOManager tests")
class IOManagerTest {
    @Test
    @DisplayName("Should expose IO utilities and XML parser singletons")
    void shouldExposeIoUtilitiesAndXmlParserSingletons() {
        assertNotNull(IOManager.INSTANCE.getIoUtils());
        assertNotNull(IOManager.INSTANCE.getIsoFilesystemParser());
        assertTrue(IOManager.INSTANCE.getIsoFilesystemParser() instanceof XMLIsoFilesystemParser);
    }

    @Test
    @DisplayName("Should expose parser utilities and configured preference store")
    void shouldExposeParserUtilitiesAndConfiguredPreferenceStore() {
        assertTrue(StringUtils.isNotBlank(IOManager.INSTANCE.getIsoFilesystemParser().generateInitialVolumeID()));
        assertNotNull(IOManager.INSTANCE.getIoUtils().getStore());
        assertTrue(IOUtils.JISOCREATOR_CONFIG_DIR.contains(".config/jisocreator"));
    }
}
