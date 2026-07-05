package cl.cavallinux.jisocreator.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
@AllArgsConstructor
public class IOUtils {
    private PreferenceStore store;
    private Properties defaultProperties;
    public final static String JISOCREATOR_CONFIG_DIR;
    private final static String JISOCREATOR_DEFAULTCONFIG_FILENAME;
    public final static String JISOCREATOR_LICENSE_FILENAME;
    public final static String JISOCREATOR_CONFIG_FILENAME;
    public final static String JISOCREATOR_MANIFEST_FILE;
    private final static String GTK_PLATFORM;
    private final static String WIN32_PLATFORM;
    private final static String WIN32_MKISOFS_BASE_PATH;

    static {
        JISOCREATOR_CONFIG_DIR = System.getProperty("user.home").concat("/.config/jisocreator/");
        JISOCREATOR_DEFAULTCONFIG_FILENAME = "conf/defaultconfig.properties";
        JISOCREATOR_CONFIG_FILENAME = "jisocreator.properties";
        JISOCREATOR_MANIFEST_FILE = "/META-INF/MANIFEST.MF";
        JISOCREATOR_LICENSE_FILENAME = "files/license.txt";
        GTK_PLATFORM = "gtk";
        WIN32_PLATFORM = "win32";
        WIN32_MKISOFS_BASE_PATH = "<mkisofs.base.path>";
    }

    public PreferenceStore getStore() {
        try {
            store.load();
        } catch (IOException e) {
            log.warn("Loading defaults", e);
            loadPreferencesFromBackup();
        }

        return store;
    }

    public void saveStore() {
        try {
            store.save();
        } catch (IOException e) {
            log.warn("Loading defaults", e);
            loadPreferencesFromBackup();
        }
    }

    public String loadFileContentFromClasspath(String filePath) {
        StringBuffer license = new StringBuffer();
        try (
            InputStream fileInputStream = ClassLoader.getSystemResourceAsStream(filePath);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader br = new BufferedReader(inputStreamReader)
        ) {
            br.lines().forEach(line -> {
                license.append(line);
                license.append("\n");
            });
        } catch (IOException | NullPointerException e) {
            log.error("Error loading file", e);
        }
        return license.toString();
    }
    
    public String loadFormattedLicenseFile() {
        String loadedLicenseFile = loadFileContentFromClasspath(IOUtils.JISOCREATOR_LICENSE_FILENAME);
        try (InputStream stream = getClass().getResource("/META-INF/MANIFEST.MF").openStream()) {
            Attributes manifestAttributes = new Manifest(stream).getMainAttributes();
            String maintainerAttribute = manifestAttributes.getValue("Maintainer");
            String programName = manifestAttributes.getValue("Implementation-Title");
            String programVersion = manifestAttributes.getValue("Implementation-Version");
            loadedLicenseFile = Strings.CI.replace(loadedLicenseFile, "<program_name>", programName);
            loadedLicenseFile = Strings.CI.replace(loadedLicenseFile, "<program_version>", programVersion);
            loadedLicenseFile = Strings.CI.replace(loadedLicenseFile, "<maintainer>", maintainerAttribute);
            loadedLicenseFile = Strings.CI.replace(loadedLicenseFile, "<year>",
                    String.valueOf(LocalDate.now().getYear()));
            return loadedLicenseFile;
        } catch (IOException e) {
            log.error("Error loading manifest file", e);
            return StringUtils.EMPTY;
        }
    }

    private void loadPreferencesFromBackup() {
        try (InputStream stream = ClassLoader.getSystemResourceAsStream(JISOCREATOR_DEFAULTCONFIG_FILENAME)) {
            Files.createDirectories(Paths.get(JISOCREATOR_CONFIG_DIR));
            defaultProperties = new Properties();
            defaultProperties.load(stream);
            log.info("Default properties loaded: {}", defaultProperties);
            store.setValue("mkisofs.rockridge.use",
                    Boolean.parseBoolean(defaultProperties.getProperty("mkisofs.rockridge.use")));
            store.setValue("mkisofs.joliet.use",
                    Boolean.parseBoolean(defaultProperties.getProperty("mkisofs.joliet.use")));
            store.setValue("mkisofs.symlinks.follow",
                    Boolean.parseBoolean(defaultProperties.getProperty("mkisofs.symlinks.follow")));
            store.setValue("general.exit.confirm",
                    Boolean.parseBoolean(defaultProperties.getProperty("general.exit.confirm")));
            store.setValue("mkisofs.path", obtainMkisofsPath(SWT.getPlatform()));
            store.setValue("mkisofs.iso.level", defaultProperties.getProperty("mkisofs.iso.level"));
            store.setValue("jisocreator.language", defaultProperties.getProperty("jisocreator.language"));
            store.save();
        } catch (IOException e) {
            log.error("Error saving properties", e);
        }
    }

    private String obtainMkisofsPath(String swtPlatform) {
        StringBuilder mkisofsPath = new StringBuilder();
        if (Strings.CI.equalsAny(swtPlatform, GTK_PLATFORM)) {
            mkisofsPath.append(defaultProperties.getProperty("mkisofs.unix.path"));
        } else if (Strings.CI.equalsAny(swtPlatform, WIN32_PLATFORM)) {
            mkisofsPath.append(Strings.CS.replace(defaultProperties.getProperty("mkisofs.win32.path"),
                    WIN32_MKISOFS_BASE_PATH, Paths.get("").toAbsolutePath().toString()));
        }
        return mkisofsPath.toString();
    }
}