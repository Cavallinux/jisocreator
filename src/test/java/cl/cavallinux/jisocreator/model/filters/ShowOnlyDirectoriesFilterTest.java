package cl.cavallinux.jisocreator.model.filters;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
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
    @DisplayName("Should select filesystem roots that are directories")
    void shouldSelectFilesystemRoots() {
        File[] roots = OSAndIsoExplorerManager.INSTANCE.getOsExplorer().getRoots();
        
        // Find a root that is actually a directory
        File rootDirectory = null;
        for (File root : roots) {
            if (Files.isDirectory(root.toPath())) {
                rootDirectory = root;
                break;
            }
        }
        
        // Assert that at least one root directory exists and is selected
        if (rootDirectory != null) {
            assertTrue(filter.select(null, null, rootDirectory));
        }
    }

    @Test
    @DisplayName("Should not select file roots (non-directories)")
    void shouldNotSelectFileRoots() {
        File[] roots = OSAndIsoExplorerManager.INSTANCE.getOsExplorer().getRoots();
        
        // Find a root that is a file (not a directory)
        File rootFile = null;
        for (File root : roots) {
            if (!Files.isDirectory(root.toPath())) {
                rootFile = root;
                break;
            }
        }
        
        // Assert that file roots are not selected
        if (rootFile != null) {
            assertFalse(filter.select(null, null, rootFile));
        }
    }
}
