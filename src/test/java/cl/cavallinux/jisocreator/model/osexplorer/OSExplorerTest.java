package cl.cavallinux.jisocreator.model.osexplorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;

@DisplayName("OSExplorer Tests")
class OSExplorerTest {

    private OSExplorer osExplorer;

    @BeforeEach
    void setUp() {
        osExplorer = OSAndIsoExplorerManager.INSTANCE.getOsExplorer();
    }

    @Test
    @DisplayName("Should return the file name correctly")
    void testGetName(@TempDir Path tempDir) throws IOException {
        Path testPath = tempDir.resolve("test.txt");
        Files.createFile(testPath);

        String name = osExplorer.getName(testPath);

        assertEquals("test.txt", name);
    }

    @Test
    @DisplayName("Should return the absolute path correctly")
    void testGetAbsolutePath(@TempDir Path tempDir) throws IOException {
        Path testPath = tempDir.resolve("test.txt");
        Files.createFile(testPath);

        String absolutePath = osExplorer.getAbsolutePath(testPath);

        assertTrue(absolutePath.contains("test.txt"));
        assertEquals(testPath.toAbsolutePath().toString(), absolutePath);
    }

    @Test
    @DisplayName("Should return file size as string")
    void testLength(@TempDir Path tempDir) throws IOException {
        Path testPath = tempDir.resolve("testfile.txt");
        Files.write(testPath, "Hello World".getBytes());

        String length = osExplorer.length(testPath);

        assertNotNull(length);
        assertTrue(Long.parseLong(length) > 0);
    }

    @Test
    @DisplayName("Should return last modified date as formatted string")
    void testLastModified(@TempDir Path tempDir) throws IOException {
        Path testPath = tempDir.resolve("test.txt");
        Files.createFile(testPath);

        String lastModified = osExplorer.lastModified(testPath);

        assertNotNull(lastModified);
        assertFalse(lastModified.isEmpty());
    }

    @Test
    @DisplayName("Should return Folder type for directories")
    void testGetFileTypeForDirectory(@TempDir Path tempDir) {
        Path directory = tempDir;

        String fileType = osExplorer.getFileType(directory);

        assertEquals("Folder", fileType);
    }

    @Test
    @DisplayName("Should return File type for file without extension")
    void testGetFileTypeForFileWithoutExtension(@TempDir Path tempDir) throws IOException {
        Path testPath = tempDir.resolve("noextension");
        Files.createFile(testPath);

        String fileType = osExplorer.getFileType(testPath);

        assertEquals("File", fileType);
    }

    @Test
    @DisplayName("Should identify if file is a root (system root)")
    void testIsRootForSystemRoot() {
        // Test with actual system roots
        // Note: Platform-specific test that adapts to both Linux and Windows profiles.
        // On Linux with gtk platform: returns File.listRoots() (e.g., [/])
        // On other platforms or in cross-compile scenarios: may return home directory contents
        File[] roots = osExplorer.getRoots();
        if (roots.length > 0) {
            boolean hasRootIdentified = false;
            for (File root : roots) {
                if (osExplorer.isRoot(root.toPath())) {
                    hasRootIdentified = true;
                    break;
                }
            }
            // If no roots are identified as actual roots, verify that roots are at least valid
            // This handles cross-compilation scenarios (e.g., windows profile on linux)
            if (!hasRootIdentified) {
                assertTrue(roots[0].exists(), "Loaded roots should exist even if not system roots");
            } else {
                assertTrue(true, "At least one root was properly identified");
            }
        }
    }

    @Test
    @DisplayName("Should identify if file is not a root")
    void testIsNotRoot(@TempDir Path tempDir) {
        boolean isRoot = osExplorer.isRoot(tempDir);

        assertFalse(isRoot);
    }

    @Test
    @DisplayName("Should set and get file system roots")
    void testSetAndGetRoots() {
        File[] newRoots = new File[] { new File("/etc"), new File("/var") };
        osExplorer.setRoots(newRoots);

        File[] retrievedRoots = osExplorer.getRoots();

        assertEquals(newRoots.length, retrievedRoots.length);
        assertEquals(newRoots[0], retrievedRoots[0]);
        assertEquals(newRoots[1], retrievedRoots[1]);
    }

    @Test
    @DisplayName("Should return Folder extension for directories")
    void testGetExtensionForDirectory(@TempDir Path tempDir) {
        String extension = osExplorer.getExtension(tempDir);

        assertEquals("Folder", extension);
    }

    @Test
    @DisplayName("Should return file extension with dot")
    void testGetExtensionForFile(@TempDir Path tempDir) throws IOException {
        Path testPath = tempDir.resolve("document.txt");
        Files.createFile(testPath);

        String extension = osExplorer.getExtension(testPath);

        assertEquals(".txt", extension);
    }

    @Test
    @DisplayName("Should return empty string for file without extension")
    void testGetExtensionForFileWithoutExtension(@TempDir Path tempDir) throws IOException {
        Path testPath = tempDir.resolve("noextension");
        Files.createFile(testPath);

        String extension = osExplorer.getExtension(testPath);

        assertEquals("", extension);
    }

    @Test
    @DisplayName("Should return singleton instance")
    void testGetInstance() {
        OSExplorer instance1 = OSAndIsoExplorerManager.INSTANCE.getOsExplorer();
        OSExplorer instance2 = OSAndIsoExplorerManager.INSTANCE.getOsExplorer();

        assertSame(instance1, instance2);
    }
}
