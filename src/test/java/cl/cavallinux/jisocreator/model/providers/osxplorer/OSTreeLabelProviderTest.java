package cl.cavallinux.jisocreator.model.providers.osxplorer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cl.cavallinux.jisocreator.model.providers.osexplorer.OSTreeLabelProvider;

@DisplayName("OSTreeLabelProvider tests")
class OSTreeLabelProviderTest {
    private final OSTreeLabelProvider provider = OSTreeLabelProvider.builder().build();

    @Test
    @DisplayName("Should use OSExplorer naming for file elements")
    void shouldUseOsExplorerNamingForFileElements(@TempDir Path tempDir) throws IOException {
        Path file = Files.write(tempDir.resolve("example.txt"), new byte[] { 1 });

        assertEquals("example.txt", provider.getText(file.toFile()));
    }
}
