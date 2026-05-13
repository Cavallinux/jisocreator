package cl.cavallinux.jisocreator.model.osexplorer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OSExplorer Tests")
class OSExplorerTest {

    private OSExplorer osExplorer;

    @BeforeEach
    void setUp() {
        osExplorer = OSExplorer.getInstance();
    }

    @Test
    @DisplayName("Should return the file name correctly")
    void testGetName(@TempDir Path tempDir) throws IOException {
        File testFile = tempDir.resolve("test.txt").toFile();
        testFile.createNewFile();

        String name = osExplorer.getName(testFile);

        assertEquals("test.txt", name);
    }

    @Test
    @DisplayName("Should return the absolute path correctly")
    void testGetAbsolutePath(@TempDir Path tempDir) throws IOException {
        File testFile = tempDir.resolve("test.txt").toFile();
        testFile.createNewFile();

        String absolutePath = osExplorer.getAbsolutePath(testFile);

        assertTrue(absolutePath.contains("test.txt"));
        assertEquals(testFile.getAbsolutePath(), absolutePath);
    }

    @Test
    @DisplayName("Should return file size as string")
    void testLength(@TempDir Path tempDir) throws IOException {
        File testFile = tempDir.resolve("testfile.txt").toFile();
        Files.write(testFile.toPath(), "Hello World".getBytes());

        String length = osExplorer.length(testFile);

        assertNotNull(length);
        assertTrue(Integer.parseInt(length) > 0);
    }

    @Test
    @DisplayName("Should return last modified date as formatted string")
    void testLastModified(@TempDir Path tempDir) throws IOException {
        File testFile = tempDir.resolve("test.txt").toFile();
        testFile.createNewFile();

        String lastModified = osExplorer.lastModified(testFile);

        assertNotNull(lastModified);
        assertFalse(lastModified.isEmpty());
    }

    @Test
    @DisplayName("Should return Folder type for directories")
    void testGetFileTypeForDirectory(@TempDir Path tempDir) {
        File directory = tempDir.toFile();

        String fileType = osExplorer.getFileType(directory);

        assertEquals("Folder", fileType);
    }

    @Test
    @DisplayName("Should return File type for file without extension")
    void testGetFileTypeForFileWithoutExtension(@TempDir Path tempDir) throws IOException {
        File testFile = tempDir.resolve("noextension").toFile();
        testFile.createNewFile();

        String fileType = osExplorer.getFileType(testFile);

        assertEquals("File", fileType);
    }

    @Test
    @DisplayName("Should identify if file is a root (system root)")
    void testIsRootForSystemRoot() {
        // Test with actual system roots
        File[] roots = osExplorer.getRoots();
        if (roots.length > 0) {
            assertTrue(osExplorer.isRoot(roots[0]));
        }
    }

    @Test
    @DisplayName("Should identify if file is not a root")
    void testIsNotRoot(@TempDir Path tempDir) {
        boolean isRoot = osExplorer.isRoot(tempDir.toFile());

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
        String extension = osExplorer.getExtension(tempDir.toFile());

        assertEquals("Folder", extension);
    }

    @Test
    @DisplayName("Should return file extension with dot")
    void testGetExtensionForFile(@TempDir Path tempDir) throws IOException {
        File testFile = tempDir.resolve("document.txt").toFile();
        testFile.createNewFile();

        String extension = osExplorer.getExtension(testFile);

        assertEquals(".txt", extension);
    }

    @Test
    @DisplayName("Should return empty string for file without extension")
    void testGetExtensionForFileWithoutExtension(@TempDir Path tempDir) throws IOException {
        File testFile = tempDir.resolve("noextension").toFile();
        testFile.createNewFile();

        String extension = osExplorer.getExtension(testFile);

        assertEquals("", extension);
    }

    @Test
    @DisplayName("Should return singleton instance")
    void testGetInstance() {
        OSExplorer instance1 = OSExplorer.getInstance();
        OSExplorer instance2 = OSExplorer.getInstance();

        assertSame(instance1, instance2);
    }
}
