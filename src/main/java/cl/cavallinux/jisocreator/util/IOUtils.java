package cl.cavallinux.jisocreator.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.lang3.Strings;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoTreeNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IOUtils {
    private PreferenceStore store;
    private Properties defaultProperties;
    private XStream xStreamParser;
    private final static IOUtils instance;
    private final static String JISOCREATOR_CONFIG_DIR;
    private final static String JISOCREATOR_DEFAULTCONFIG_FILENAME;
    private final static String JISOCREATOR_CONFIG_FILENAME;
    private final static String GTK_PLATFORM;
    private final static String WIN32_PLATFORM;
    private final static String WIN32_MKISOFS_BASE_PATH;

    static {
        JISOCREATOR_CONFIG_DIR = System.getProperty("user.home").concat("/.config/jisocreator/");
        JISOCREATOR_DEFAULTCONFIG_FILENAME = "defaultconfig.properties";
        JISOCREATOR_CONFIG_FILENAME = "jisocreator.properties";
        GTK_PLATFORM = "gtk";
        WIN32_PLATFORM = "win32";
        WIN32_MKISOFS_BASE_PATH="<mkisofs.base.path>";
        instance = new IOUtils();
    }

    private IOUtils() {
        loadXMLParser();
    }

    public static IOUtils getInstance() {
        return instance;
    }

    public Object parseXMLFileToObject(String path) {
        try (FileInputStream fis = new FileInputStream(path)) {
            IsoFileSystem iso = new IsoFileSystem();
            xStreamParser.fromXML(fis, iso);
            return iso;
        } catch (IOException e) {
            log.error("Error parsing XML", e);
            return null;
        }
    }

    public boolean saveObjectToXML(Object objectToParse, String path, IProgressMonitor monitor) {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            monitor.subTask("Parsing XML...");
            xStreamParser.toXML(objectToParse, fos);
            return true;
        } catch (IOException e) {
            log.error("Error saving XML", e);
            return false;
        }
    }

    public PreferenceStore getStore() {
        store = new PreferenceStore(JISOCREATOR_CONFIG_DIR.concat(JISOCREATOR_CONFIG_FILENAME));
        try {
            store.load();
        } catch (IOException e) {
            log.warn("Loading defaults", e);
            loadPreferencesFromBackup();
        }

        return store;
    }

    private void loadXMLParser() {
        xStreamParser = new XStream(new DomDriver());

        xStreamParser.alias("iso9660", IsoFileSystem.class);
        xStreamParser.alias("entry", IsoTreeNode.class);
        xStreamParser.aliasAttribute(IsoFileSystem.class, "root", "RootEntry");
        xStreamParser.aliasAttribute(IsoFileSystem.class, "volumeID", "volumeid");
        xStreamParser.aliasAttribute(IsoFileSystem.class, "applicationID", "applicationid");
        xStreamParser.aliasAttribute(IsoFileSystem.class, "isoLength", "isolength");
        xStreamParser.aliasAttribute(IsoTreeNode.class, "isRoot", "root");
        xStreamParser.aliasAttribute(IsoTreeNode.class, "isoName", "isoname");
        xStreamParser.useAttributeFor(IsoFileSystem.class, "volumeID");
        xStreamParser.useAttributeFor(IsoFileSystem.class, "applicationID");
        xStreamParser.useAttributeFor(IsoFileSystem.class, "isoLength");
        xStreamParser.useAttributeFor(IsoTreeNode.class, "file");
        xStreamParser.useAttributeFor(IsoTreeNode.class, "isRoot");
        xStreamParser.useAttributeFor(IsoTreeNode.class, "isoName");
    }

    private void loadPreferencesFromBackup() {
        try (InputStream stream = getClass().getResourceAsStream("res/conf/".concat(JISOCREATOR_DEFAULTCONFIG_FILENAME))) {
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