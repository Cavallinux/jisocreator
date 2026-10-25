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

import org.eclipse.swt.program.Program;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import cl.cavallinux.jisocreator.testsupport.SwtPlatformAssumptions;

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
        assertTrue(lastModified.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"),
                "Expected format 'yyyy-MM-dd HH:mm:ss' but was: " + lastModified);
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
        /**
         * OSExplorer is now always initialized with File.listRoots(), so all roots
         * returned by getRoots() must be identifiable as roots via isRoot().
         */
        File[] roots = osExplorer.getRoots();
        assertTrue(roots.length > 0, "File.listRoots() should return at least one root");
        for (File root : roots) {
            assertTrue(osExplorer.isRoot(root.toPath()),
                    () -> "Expected " + root + " to be identified as a root");
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

    @Test
    @DisplayName("Should return the first file system root as the Unix OS root")
    void testGetUnixOSRoot() {
        File[] newRoots = new File[] { new File("/"), new File("/mnt") };
        osExplorer.setRoots(newRoots);

        File unixOSRoot = osExplorer.getUnixOSRoot();

        assertEquals(newRoots[0], unixOSRoot);
    }

    @Test
    @DisplayName("Should cache findProgram results per extension, avoiding repeated native lookups")
    void testFindProgramCachesResultsPerExtension() {
        SwtPlatformAssumptions.assumeNativePlatformMatches();

        Program first = osExplorer.findProgram(".txt");
        Program second = osExplorer.findProgram(".txt");

        /**
         * A cache hit must return the exact same instance stored from the first
         * (real) native lookup, rather than triggering Program.findProgram again.
         */
        assertSame(first, second);
    }

    @Test
    @DisplayName("Should return the file type resolved via the cached findProgram lookup")
    void testGetFileTypeForFileWithExtensionUsesCachedProgram(@TempDir Path tempDir) throws IOException {
        SwtPlatformAssumptions.assumeNativePlatformMatches();

        Path testPath = tempDir.resolve("document.txt");
        Files.createFile(testPath);

        String fileType = osExplorer.getFileType(testPath);

        assertNotNull(fileType);
        assertFalse(fileType.isBlank());
    }

    @Test
    @DisplayName("Should populate the attributes cache for every entry of a directory listing")
    void testWarmAttributesCachePopulatesEntriesForDirectoryListing(@TempDir Path tempDir) throws IOException {
        Path subDirectory = Files.createDirectory(tempDir.resolve("sub-dir"));
        Path subFile = Files.write(tempDir.resolve("sub-file.txt"), "Hello".getBytes());

        osExplorer.warmAttributesCache(tempDir);

        assertTrue(osExplorer.isDirectory(subDirectory));
        assertFalse(osExplorer.isDirectory(subFile));
        assertEquals(Long.toString(Files.size(subFile)), osExplorer.length(subFile));
    }

    @Test
    @DisplayName("Should serve isDirectory/length/lastModified from the warmed cache without re-reading the filesystem")
    void testIsDirectoryUsesCachedAttributesAfterFileDeleted(@TempDir Path tempDir) throws IOException {
        Path subFile = Files.write(tempDir.resolve("cached-file.txt"), "Hello World".getBytes());
        osExplorer.warmAttributesCache(tempDir);

        /**
         * Delete the file after warming the cache: a fresh (uncached) stat call
         * would now fail/return a fallback value, so if the cached values are
         * still returned correctly, the cache (not a fresh filesystem call) is
         * clearly what served the result.
         */
        Files.delete(subFile);

        assertFalse(osExplorer.isDirectory(subFile));
        assertEquals(Long.toString("Hello World".getBytes().length), osExplorer.length(subFile));
    }

    @Test
    @DisplayName("Should keep multiple recently warmed directories cached (LRU) instead of discarding previous ones")
    void testWarmAttributesCacheKeepsMultipleRecentDirectoriesCached(@TempDir Path tempDir) throws IOException {
        Path firstDir = Files.createDirectory(tempDir.resolve("first-dir"));
        Path firstDirFile = Files.write(firstDir.resolve("first-file.txt"), new byte[] { 1 });
        Path secondDir = Files.createDirectory(tempDir.resolve("second-dir"));
        Path secondDirFile = Files.write(secondDir.resolve("second-file.txt"), new byte[] { 1, 2 });

        osExplorer.warmAttributesCache(firstDir);
        osExplorer.warmAttributesCache(secondDir);
        Files.delete(firstDirFile);

        /**
         * Both directories fit well within MAX_CACHED_DIRECTORIES, so warming
         * secondDir must NOT evict firstDir's cached entries: isDirectory() for
         * the now-deleted firstDirFile must still be served from the (still
         * present) cache instead of falling back to a failing filesystem check.
         */
        assertFalse(osExplorer.isDirectory(firstDirFile));
        assertEquals(Long.toString(1), osExplorer.length(firstDirFile));
        assertEquals(Long.toString(Files.size(secondDirFile)), osExplorer.length(secondDirFile));
    }

    @Test
    @DisplayName("Should evict the least-recently-warmed directory once the LRU capacity is exceeded")
    void testWarmAttributesCacheEvictsLeastRecentlyWarmedDirectoryBeyondCapacity(@TempDir Path tempDir)
            throws IOException {
        Path leastRecentDir = Files.createDirectory(tempDir.resolve("dir-0"));
        Path leastRecentDirFile = Files.write(leastRecentDir.resolve("file.txt"), new byte[] { 1 });
        osExplorer.warmAttributesCache(leastRecentDir);

        /**
         * Warm MAX_CACHED_DIRECTORIES (5) additional, distinct directories so the
         * very first one (leastRecentDir) is pushed out of the LRU cache.
         */
        for (int i = 1; i <= 5; i++) {
            Path directory = Files.createDirectory(tempDir.resolve("dir-" + i));
            Files.write(directory.resolve("file.txt"), new byte[] { 1 });
            osExplorer.warmAttributesCache(directory);
        }

        Files.delete(leastRecentDirFile);

        /**
         * leastRecentDir's cached entries were evicted once the LRU capacity was
         * exceeded, so length() must fall back to a fresh (now-failing)
         * filesystem check for the now-deleted path (java.io.File#length()
         * returns 0 for a non-existent file), rather than the stale cached size
         * of 1 byte that a still-present cache entry would have returned.
         */
        assertEquals(Long.toString(0), osExplorer.length(leastRecentDirFile));
    }

    @Test
    @DisplayName("Should follow symbolic links to directories when warming the attributes cache")
    void testWarmAttributesCacheFollowsSymbolicLinksToDirectories(@TempDir Path tempDir) throws IOException {
        Path realDirectory = Files.createDirectory(tempDir.resolve("real-dir"));
        Path symlinkToDirectory = tempDir.resolve("link-to-dir");
        createSymbolicLinkOrAssumeUnsupported(symlinkToDirectory, realDirectory);

        osExplorer.warmAttributesCache(tempDir);

        /**
         * Regression test: warmAttributesCache used to read attributes with
         * LinkOption.NOFOLLOW_LINKS, which reports a symbolic link itself as
         * never being a directory (even when it points to one), causing
         * directories reached via a symlink to incorrectly render as files in
         * the OS explorer. Attributes must now be resolved by following links,
         * matching isDirectory()'s uncached fallback (plain Files.isDirectory).
         */
        assertTrue(osExplorer.isDirectory(symlinkToDirectory));
    }

    @Test
    @DisplayName("Should follow symbolic links to directories when isDirectory falls back to an uncached check")
    void testIsDirectoryFollowsSymbolicLinksToDirectoriesWithoutWarmedCache(@TempDir Path tempDir)
            throws IOException {
        Path realDirectory = Files.createDirectory(tempDir.resolve("real-dir"));
        Path symlinkToDirectory = tempDir.resolve("link-to-dir");
        createSymbolicLinkOrAssumeUnsupported(symlinkToDirectory, realDirectory);

        assertTrue(osExplorer.isDirectory(symlinkToDirectory));
    }

    private static void createSymbolicLinkOrAssumeUnsupported(Path link, Path target) throws IOException {
        try {
            Files.createSymbolicLink(link, target);
        } catch (UnsupportedOperationException | IOException e) {
            org.junit.jupiter.api.Assumptions.assumeTrue(false,
                    "Symbolic links are not supported in this environment: " + e.getMessage());
        }
    }}
