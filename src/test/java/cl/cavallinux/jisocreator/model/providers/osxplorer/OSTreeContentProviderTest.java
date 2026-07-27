package cl.cavallinux.jisocreator.model.providers.osxplorer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import cl.cavallinux.jisocreator.model.providers.osexplorer.OSTreeContentProvider;

@DisplayName("OSTreeContentProvider tests")
class OSTreeContentProviderTest {
    private final OSTreeContentProvider provider = OSTreeContentProvider.builder().build();

    @Test
    @DisplayName("Should return filesystem roots for non-file input")
    void shouldReturnFilesystemRootsForNonFileInput() {
        Object[] rootsFromProvider = provider.getChildren("roots");
        Object[] rootsFromManager = OSAndIsoExplorerManager.INSTANCE.getOsExplorer().getRoots();

        assertArrayEquals(rootsFromManager, rootsFromProvider);
        assertArrayEquals(rootsFromManager, provider.getElements("roots"));
    }

    @Test
    @DisplayName("Should return children and parent for directory input")
    void shouldReturnChildrenAndParentForDirectoryInput(@TempDir Path tempDir) throws IOException {
        Path childDir = Files.createDirectory(tempDir.resolve("child-dir"));
        Path childFile = Files.write(tempDir.resolve("child.txt"), new byte[] { 1 });

        Object[] children = provider.getChildren(tempDir.toFile());
        Set<String> names = Arrays.stream(children).map(File.class::cast).map(File::getName).collect(Collectors.toSet());

        assertTrue(names.contains(childDir.getFileName().toString()));
        assertTrue(names.contains(childFile.getFileName().toString()));
        assertEquals(tempDir.getParent().toFile(), provider.getParent(tempDir.toFile()));
        assertTrue(provider.hasChildren(tempDir.toFile()));
    }

    @Test
    @DisplayName("Should handle regular file input with no children")
    void shouldHandleRegularFileInputWithNoChildren(@TempDir Path tempDir) throws IOException {
        Path file = Files.write(tempDir.resolve("plain.txt"), new byte[] { 1, 2 });

        Object[] children = provider.getChildren(file.toFile());
        assertNull(children, "getChildren should return null for regular files");
        assertNotNull(provider.getParent(file.toFile()));
        assertFalse(provider.hasChildren(file.toFile()));
    }

    @Test
    @DisplayName("Should report no children for an empty directory without reading a full listing")
    void shouldReportNoChildrenForEmptyDirectory(@TempDir Path tempDir) throws IOException {
        Path emptyDir = Files.createDirectory(tempDir.resolve("empty-dir"));

        assertFalse(provider.hasChildren(emptyDir.toFile()));
    }

    @Test
    @DisplayName("Should report children present as soon as a single entry exists")
    void shouldReportChildrenPresentWithSingleEntry(@TempDir Path tempDir) throws IOException {
        Path dirWithOneChild = Files.createDirectory(tempDir.resolve("one-child-dir"));
        Files.write(dirWithOneChild.resolve("only-file.txt"), new byte[] { 1 });

        assertTrue(provider.hasChildren(dirWithOneChild.toFile()));
    }
}