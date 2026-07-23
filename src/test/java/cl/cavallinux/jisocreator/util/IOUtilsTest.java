package cl.cavallinux.jisocreator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.jface.preference.PreferenceStore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests para los métodos de {@link IOUtils} no cubiertos por
 * {@link IOUtilsPathTest} (que solo valida manipulación de rutas de archivo).
 */
@DisplayName("IOUtils tests")
class IOUtilsTest {

    @Test
    @DisplayName("Should load an existing preference store without falling back to defaults")
    void shouldLoadExistingPreferenceStoreWithoutFallback(@TempDir Path tempDir) throws IOException {
        Path storeFile = tempDir.resolve("jisocreator.properties");
        PreferenceStore preExisting = new PreferenceStore(storeFile.toString());
        preExisting.setValue("mkisofs.iso.level", "2");
        preExisting.save();

        IOUtils ioUtils = IOUtils.builder()
                .store(new PreferenceStore(storeFile.toString()))
                .build();

        PreferenceStore loaded = ioUtils.getStore();

        assertNotNull(loaded);
        assertEquals("2", loaded.getString("mkisofs.iso.level"));
    }

    @Test
    @DisplayName("Should fall back to default properties when the store file does not exist")
    void shouldFallBackToDefaultsWhenStoreFileMissing(@TempDir Path tempDir) {
        Path storeFile = tempDir.resolve("missing").resolve("jisocreator.properties");

        IOUtils ioUtils = IOUtils.builder()
                .store(new PreferenceStore(storeFile.toString()))
                .build();

        PreferenceStore loaded = ioUtils.getStore();

        assertNotNull(loaded);
        assertEquals(3, loaded.getInt("mkisofs.iso.level"));
        assertTrue(loaded.getBoolean("mkisofs.rockridge.use"));
        assertTrue(loaded.getBoolean("mkisofs.joliet.use"));
        assertFalse(loaded.getBoolean("mkisofs.symlinks.follow"));
        assertTrue(loaded.getBoolean("general.exit.confirm"));
        assertEquals("en", loaded.getString("jisocreator.language"));
    }

    @Test
    @DisplayName("Should persist store changes to disk")
    void shouldPersistStoreChangesToDisk(@TempDir Path tempDir) {
        Path storeFile = tempDir.resolve("jisocreator.properties");
        PreferenceStore store = new PreferenceStore(storeFile.toString());
        store.setValue("mkisofs.iso.level", "1");

        IOUtils ioUtils = IOUtils.builder().store(store).build();
        ioUtils.saveStore();

        assertTrue(storeFile.toFile().exists());
    }

    @Test
    @DisplayName("Should load file content from the classpath")
    void shouldLoadFileContentFromClasspath() {
        IOUtils ioUtils = IOUtils.builder().store(new PreferenceStore()).build();

        String license = ioUtils.loadFileContentFromClasspath(IOUtils.JISOCREATOR_LICENSE_FILENAME);

        assertNotNull(license);
        assertFalse(license.isBlank());
    }

    @Test
    @DisplayName("Should return an empty string when the classpath resource does not exist")
    void shouldReturnEmptyStringForMissingClasspathResource() {
        IOUtils ioUtils = IOUtils.builder().store(new PreferenceStore()).build();

        String content = ioUtils.loadFileContentFromClasspath("does/not/exist.txt");

        assertEquals("", content);
    }

    @Test
    @DisplayName("Should load the formatted license file without throwing even without a runtime manifest")
    void shouldLoadFormattedLicenseFileWithoutThrowing() {
        IOUtils ioUtils = IOUtils.builder().store(new PreferenceStore()).build();

        String formattedLicense = ioUtils.loadFormattedLicenseFile();

        assertNotNull(formattedLicense);
        assertFalse(formattedLicense.isBlank());
    }
}
