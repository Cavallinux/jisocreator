package cl.cavallinux.jisocreator.model.filters;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;

@DisplayName("ShowOnlyDirectoriesFilter tests")
class ShowOnlyDirectoriesFilterTest {
    private final ShowOnlyDirectoriesFilter filter = new ShowOnlyDirectoriesFilter();

    @Test
    @DisplayName("Should select directories")
    void shouldSelectDirectories(@TempDir Path tempDir) throws IOException {
        Path directory = Files.createDirectory(tempDir.resolve("dir"));

        assertTrue(filter.select(null, null, directory.toFile()));
    }

    @Test
    @DisplayName("Should not select regular files")
    void shouldNotSelectRegularFiles(@TempDir Path tempDir) throws IOException {
        Path file = Files.write(tempDir.resolve("file.txt"), new byte[] { 1 });

        assertFalse(filter.select(null, null, file.toFile()));
    }

    @Test
    @DisplayName("Should select filesystem roots")
    void shouldSelectFilesystemRoots() {
        Object[] roots = OSAndIsoExplorerManager.INSTANCE.getOsExplorer().getRoots();
        Path root = ((java.io.File) roots[0]).toPath();

        assertTrue(filter.select(null, null, root));
    }
}
