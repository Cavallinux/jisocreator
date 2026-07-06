package cl.cavallinux.jisocreator.model.comparators;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
