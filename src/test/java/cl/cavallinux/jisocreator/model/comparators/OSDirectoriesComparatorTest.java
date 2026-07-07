package cl.cavallinux.jisocreator.model.comparators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("OSDirectoriesComparator tests")
class OSDirectoriesComparatorTest {
    private final OSDirectoriesComparator comparator = new OSDirectoriesComparator();

    @Test
    @DisplayName("Should categorize directories before files")
    void shouldCategorizeDirectoriesBeforeFiles(@TempDir Path tempDir) throws IOException {
        Path directory = Files.createDirectory(tempDir.resolve("folder"));
        Path file = Files.write(tempDir.resolve("file.txt"), new byte[] { 1 });

        assertEquals(0, comparator.category(directory.toFile()));
        assertEquals(1, comparator.category(file.toFile()));
    }

    @Test
    @DisplayName("Should sort directory before file")
    void shouldSortDirectoryBeforeFile(@TempDir Path tempDir) throws IOException {
        Path directory = Files.createDirectory(tempDir.resolve("z_folder"));
        Path file = Files.write(tempDir.resolve("a_file.txt"), new byte[] { 1 });

        int result = comparator.compare(null, directory.toFile(), file.toFile());

        assertTrue(result < 0, "Directory should come before file");
    }

    @Test
    @DisplayName("Should sort file after directory")
    void shouldSortFileAfterDirectory(@TempDir Path tempDir) throws IOException {
        Path directory = Files.createDirectory(tempDir.resolve("z_folder"));
        Path file = Files.write(tempDir.resolve("a_file.txt"), new byte[] { 1 });

        int result = comparator.compare(null, file.toFile(), directory.toFile());

        assertTrue(result > 0, "File should come after directory");
    }

    @Test
    @DisplayName("Should sort two directories alphabetically")
    void shouldSortTwoDirectoriesAlphabetically(@TempDir Path tempDir) throws IOException {
        Path dirA = Files.createDirectory(tempDir.resolve("a_folder"));
        Path dirB = Files.createDirectory(tempDir.resolve("b_folder"));

        int result = comparator.compare(null, dirA.toFile(), dirB.toFile());

        assertTrue(result < 0, "Directory 'a' should come before directory 'b'");
    }

    @Test
    @DisplayName("Should sort two files alphabetically")
    void shouldSortTwoFilesAlphabetically(@TempDir Path tempDir) throws IOException {
        Path fileA = Files.write(tempDir.resolve("a_file.txt"), new byte[] { 1 });
        Path fileB = Files.write(tempDir.resolve("b_file.txt"), new byte[] { 1 });

        int result = comparator.compare(null, fileA.toFile(), fileB.toFile());

        assertTrue(result < 0, "File 'a' should come before file 'b'");
    }
}
