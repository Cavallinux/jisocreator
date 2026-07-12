package cl.cavallinux.jisocreator.instances;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.model.cmdline.JISOCreatorCommandLineParser;

@DisplayName("CommandLineParserManager tests")
class CommandLineParserManagerTest {
    @Test
    @DisplayName("Should expose singleton parser instance with expected implementation")
    void shouldExposeSingletonParserInstanceWithExpectedImplementation() {
        assertNotNull(CommandLineParserManager.INSTANCE.getParser());
        assertTrue(CommandLineParserManager.INSTANCE.getParser() instanceof JISOCreatorCommandLineParser);
        assertSame(CommandLineParserManager.INSTANCE.getParser(), CommandLineParserManager.INSTANCE.getParser());
    }
}
