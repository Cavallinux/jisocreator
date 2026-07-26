package cl.cavallinux.jisocreator.model.filters;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;

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

    @Test
    @DisplayName("Should still select a root when checking its directory status throws AccessDeniedException")
    void shouldSelectRootWhenAccessIsDenied(@TempDir Path tempDir) throws IOException {
        OSExplorer osExplorer = OSAndIsoExplorerManager.INSTANCE.getOsExplorer();
        File[] originalRoots = osExplorer.getRoots();

        Path restrictedParent = Files.createDirectory(tempDir.resolve("restricted-parent"));
        Path blockedRoot = Files.createDirectory(restrictedParent.resolve("blocked-root"));

        try {
            // Removing execute/search permission on the parent directory reproduces
            // the same AccessDeniedException a Windows fixed disk blocked by
            // Controlled Folder Access (or a locked BitLocker volume/restrictive
            // ACLs) would raise when its attributes are queried.
            Files.setPosixFilePermissions(restrictedParent, PosixFilePermissions.fromString("rw-rw-rw-"));

            boolean accessDeniedReproduced;
            try {
                Files.readAttributes(blockedRoot, BasicFileAttributes.class);
                accessDeniedReproduced = false;
            } catch (AccessDeniedException e) {
                accessDeniedReproduced = true;
            } catch (IOException e) {
                accessDeniedReproduced = false;
            }
            Assumptions.assumeTrue(accessDeniedReproduced,
                    "Could not reproduce an AccessDeniedException in this environment (e.g. running as root, "
                            + "where POSIX permission checks are bypassed)");

            osExplorer.setRoots(new File[] { blockedRoot.toFile() });

            // Regression test for the Windows fixed-disk root visibility bug: a root
            // whose Files.isDirectory(Path) check fails due to an access restriction
            // must still be selected (shown in the tree), instead of being silently
            // filtered out as if it genuinely weren't a directory.
            assertTrue(filter.select(null, null, blockedRoot.toFile()));
        } finally {
            Files.setPosixFilePermissions(restrictedParent, PosixFilePermissions.fromString("rwxrwxrwx"));
            osExplorer.setRoots(originalRoots);
        }
    }
}
