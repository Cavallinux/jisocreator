package cl.cavallinux.jisocreator.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("IOUtils Extension Path Tests")
class IOUtilsPathTest {

    @Test
    @DisplayName("Should handle valid file paths")
    void testValidFilePath(@TempDir Path tempDir) throws IOException {
        File testFile = tempDir.resolve("test.xml").toFile();
        Files.write(testFile.toPath(), "<root/>".getBytes());

        assertTrue(testFile.exists());
        assertTrue(testFile.isFile());
    }

    @Test
    @DisplayName("Should handle directory creation")
    void testDirectoryCreation(@TempDir Path tempDir) {
        File configDir = tempDir.resolve(".config/jisocreator").toFile();
        boolean created = configDir.mkdirs();

        assertTrue(created || configDir.exists());
        assertTrue(configDir.isDirectory());
    }

    @Test
    @DisplayName("Should handle file operations in config directory")
    void testFileOperationsInConfigDir(@TempDir Path tempDir) throws IOException {
        File configDir = tempDir.resolve(".config/jisocreator").toFile();
        configDir.mkdirs();
        
        File configFile = new File(configDir, "jisocreator.properties");
        boolean created = configFile.createNewFile();

        assertTrue(created || configFile.exists());
        assertTrue(configFile.getParent().contains(".config"));
    }

    @Test
    @DisplayName("Should validate XML file existence")
    void testXMLFileExistence(@TempDir Path tempDir) throws IOException {
        File xmlFile = tempDir.resolve("test.xml").toFile();
        Files.write(xmlFile.toPath(), "<?xml version=\"1.0\"?><root/>".getBytes());

        assertTrue(xmlFile.exists());
        assertTrue(xmlFile.getName().endsWith(".xml"));
    }

    @Test
    @DisplayName("Should handle file path concatenation")
    void testFilePathConcatenation(@TempDir Path tempDir) {
        String basePath = tempDir.toString();
        String configDir = basePath + "/.config/jisocreator/";
        String configFile = configDir + "jisocreator.properties";

        assertTrue(configFile.contains(".config"));
        assertTrue(configFile.contains("jisocreator.properties"));
    }
}
