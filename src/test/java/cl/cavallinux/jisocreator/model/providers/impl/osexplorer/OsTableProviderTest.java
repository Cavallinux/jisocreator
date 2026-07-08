package cl.cavallinux.jisocreator.model.providers.impl.osexplorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cl.cavallinux.jisocreator.model.providers.osexplorer.OsTableProvider;

@DisplayName("OsTableProvider tests")
class OsTableProviderTest {
    private final OsTableProvider provider = OsTableProvider.builder().build();

    @Test
    @DisplayName("Should list directory entries from File input")
    void shouldListDirectoryEntriesFromFileInput(@TempDir Path tempDir) throws IOException {
        Path childA = Files.createDirectory(tempDir.resolve("a"));
        Path childB = Files.write(tempDir.resolve("b"), new byte[] { 7 });

        Object[] elements = provider.getElements(tempDir.toFile());
        Set<String> names = Arrays.stream(elements).map(java.io.File.class::cast).map(java.io.File::getName)
                .collect(Collectors.toSet());

        assertTrue(names.contains(childA.getFileName().toString()));
        assertTrue(names.contains(childB.getFileName().toString()));
    }

    @Test
    @DisplayName("Should list directory entries from Path input and return empty for invalid listing")
    void shouldListDirectoryEntriesFromPathInputAndReturnEmptyForInvalidListing(@TempDir Path tempDir)
            throws IOException {
        Path child = Files.write(tempDir.resolve("file1"), new byte[] { 1 });
        Object[] fromPath = provider.getElements(tempDir);
        Set<String> fileNames = Arrays.stream(fromPath).map(Path.class::cast).map(Path::getFileName).map(Path::toString)
                .collect(Collectors.toSet());
        assertTrue(fileNames.contains(child.getFileName().toString()));

        Object[] invalid = provider.getElements(child);
        assertEquals(0, invalid.length);
    }

    @Test
    @DisplayName("Should provide table text values and null image for non-icon columns")
    void shouldProvideTableTextValuesAndNullImageForNonIconColumns(@TempDir Path tempDir) throws IOException {
        Path file = Files.write(tempDir.resolve("entry"), new byte[] { 1, 2, 3, 4 });

        assertEquals("entry", provider.getColumnText(file, 0));
        assertEquals("File", provider.getColumnText(file, 1));
        assertEquals("4", provider.getColumnText(file, 2));
        assertEquals("", provider.getColumnText(file, 99));
        assertNull(provider.getColumnImage(file, 3));
    }
}
