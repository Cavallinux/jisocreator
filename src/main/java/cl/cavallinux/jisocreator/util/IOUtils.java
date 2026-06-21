package cl.cavallinux.jisocreator.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

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
    public final static String JISOCREATOR_CONFIG_FILENAME;
    private final static String GTK_PLATFORM;
    private final static String WIN32_PLATFORM;
    private final static String WIN32_MKISOFS_BASE_PATH;

    static {
        JISOCREATOR_CONFIG_DIR = System.getProperty("user.home").concat("/.config/jisocreator/");
        JISOCREATOR_DEFAULTCONFIG_FILENAME = "defaultconfig.properties";
        JISOCREATOR_CONFIG_FILENAME = "jisocreator.properties";
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

    private void loadPreferencesFromBackup() {
        try (InputStream stream = getClass()
                .getResourceAsStream("res/conf/".concat(JISOCREATOR_DEFAULTCONFIG_FILENAME))) {
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