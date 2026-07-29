package cl.cavallinux.jisocreator.model.comparators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
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

    @Test
    @DisplayName("Should place all directories before files when sorting a mixed listing")
    void shouldSortMixedListingWithDirectoriesFirst(@TempDir Path tempDir) throws IOException {
        Path[] elements = new Path[10];
        for (int i = 0; i < 5; i++) {
            elements[i] = Files.write(tempDir.resolve("file_" + i + ".txt"), new byte[] { 1 });
        }
        for (int i = 5; i < 10; i++) {
            elements[i] = Files.createDirectory(tempDir.resolve("folder_" + i));
        }

        Object[] files = java.util.Arrays.stream(elements).map(Path::toFile).toArray();
        comparator.sort(null, files);

        boolean sawFileAfterDirectorySwitch = false;
        boolean directorySeen = false;
        for (Object element : files) {
            boolean isDirectory = ((File) element).isDirectory();
            if (!isDirectory) {
                directorySeen = true;
            } else if (directorySeen) {
                sawFileAfterDirectorySwitch = true;
            }
        }
        assertFalse(sawFileAfterDirectorySwitch, "No directory should appear after a file in the sorted result");

        for (int i = 0; i < 5; i++) {
            assertTrue(((File) files[i]).isDirectory(), "First half of sorted elements should be directories");
        }
        for (int i = 5; i < 10; i++) {
            assertFalse(((File) files[i]).isDirectory(), "Second half of sorted elements should be files");
        }
    }

    @Test
    @DisplayName("Should still compute correct categories outside of a sort() call")
    void shouldComputeCategoryCorrectlyWithoutSortCall(@TempDir Path tempDir) throws IOException {
        Path directory = Files.createDirectory(tempDir.resolve("standalone-folder"));
        Path file = Files.write(tempDir.resolve("standalone-file.txt"), new byte[] { 1 });

        /**
         * category()/compare() may be invoked directly (as in the tests above),
         * outside of a sort() call, in which case no per-sort cache is active.
         */
        assertEquals(0, comparator.category(directory.toFile()));
        assertEquals(1, comparator.category(file.toFile()));
    }
}
