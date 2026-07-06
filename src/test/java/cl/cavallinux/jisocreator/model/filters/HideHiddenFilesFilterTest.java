package cl.cavallinux.jisocreator.model.filters;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("HideHiddenFilesFilter tests")
class HideHiddenFilesFilterTest {
    private final HideHiddenFilesFilter filter = new HideHiddenFilesFilter();

    @Test
    @DisplayName("Should select visible file")
    void shouldSelectVisibleFile(@TempDir Path tempDir) throws IOException {
        Path visibleFile = Files.write(tempDir.resolve("visible.txt"), new byte[] { 1 });

        assertTrue(filter.select(null, null, visibleFile.toFile()));
    }

    @Test
    @DisplayName("Should not select hidden file")
    void shouldNotSelectHiddenFile(@TempDir Path tempDir) throws IOException {
        Path hiddenFile = Files.write(tempDir.resolve(".hidden.txt"), new byte[] { 1 });

        assertFalse(filter.select(null, null, hiddenFile.toFile()));
    }

}
