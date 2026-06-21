package cl.cavallinux.jisocreator.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoTreeNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IOUtils {
    private PreferenceStore store;
    private Properties defaultProperties;
    @Deprecated(since = "0.1.6", forRemoval = true)
    private XStream xStreamParser;
    private final static String JISOCREATOR_CONFIG_DIR;
    private final static String JISOCREATOR_DEFAULTCONFIG_FILENAME;
    private final static String JISOCREATOR_CONFIG_FILENAME;
    private final static String GTK_PLATFORM;
    private final static String WIN32_PLATFORM;
    private final static String WIN32_MKISOFS_BASE_PATH;
    public final static int MKISOFS_VOLUMEID_MAXLENGTH;
    private final static String MKISOFS_ISOFILESYSTEM_APPLICATIONID;

    static {
        JISOCREATOR_CONFIG_DIR = System.getProperty("user.home").concat("/.config/jisocreator/");
        JISOCREATOR_DEFAULTCONFIG_FILENAME = "defaultconfig.properties";
        JISOCREATOR_CONFIG_FILENAME = "jisocreator.properties";
        GTK_PLATFORM = "gtk";
        WIN32_PLATFORM = "win32";
        WIN32_MKISOFS_BASE_PATH = "<mkisofs.base.path>";
        MKISOFS_VOLUMEID_MAXLENGTH = 32;
        MKISOFS_ISOFILESYSTEM_APPLICATIONID = String.format("%s", IOUtils.class.getPackage().getImplementationTitle());
    }

    public IOUtils() {
        loadXMLParser();
    }

    public String generateInitialVolumeID() {
        String isoFileSystemVolumeID = UUID.randomUUID().toString();
        isoFileSystemVolumeID = isoFileSystemVolumeID.replace("-", "");
        return isoFileSystemVolumeID.substring(0, Math.min(isoFileSystemVolumeID.length(), MKISOFS_VOLUMEID_MAXLENGTH));
    }

    public String generateIsoFilesystemApplicationID() {
        return MKISOFS_ISOFILESYSTEM_APPLICATIONID;
    }

    @Deprecated(since = "0.1.6", forRemoval = true)
    public Object parseXMLFileToObject(String path) {
        try (FileInputStream fis = new FileInputStream(path)) {
            IsoFileSystem iso = (IsoFileSystem) xStreamParser.fromXML(fis);
            repairApplicationIDAndPublisherID(iso);
            return iso;
        } catch (IOException | XStreamException e) {
            log.error("Error parsing XML", e);
            return null;
        }
    }

    @Deprecated(since = "0.1.6", forRemoval = true)
    public boolean saveObjectToXML(Object objectToParse, String path) {
        try (FileOutputStream fos = new FileOutputStream(path)) {
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

    public void saveStore() {
        try {
            store.save();
        } catch (IOException e) {
            log.warn("Loading defaults", e);
            loadPreferencesFromBackup();
        }
    }

    @Deprecated(since = "0.1.6", forRemoval = true)
    private void loadXMLParser() {
        xStreamParser = new XStream(new PureJavaReflectionProvider());
        xStreamParser.addPermission(NoTypePermission.NONE);
        xStreamParser.addPermission(NullPermission.NULL);
        xStreamParser.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xStreamParser.allowTypesByWildcard(new String[] { "cl.cavallinux.jisocreator.model.isoexplorer.impl.**" });

        xStreamParser.alias("iso9660", IsoFileSystem.class);
        xStreamParser.alias("entry", IsoTreeNode.class);
        xStreamParser.aliasAttribute(IsoFileSystem.class, "root", "RootEntry");
        xStreamParser.aliasAttribute(IsoFileSystem.class, "volumeID", "volumeid");
        xStreamParser.aliasAttribute(IsoFileSystem.class, "applicationID", "applicationid");
        xStreamParser.aliasAttribute(IsoFileSystem.class, "isoLength", "isolength");
        xStreamParser.aliasAttribute(IsoFileSystem.class, "publisherID", "publisherid");
        xStreamParser.aliasAttribute(IsoFileSystem.class, "isoLength", "isolength");
        xStreamParser.aliasAttribute(IsoTreeNode.class, "isRoot", "root");
        xStreamParser.aliasAttribute(IsoTreeNode.class, "isoName", "isoname");
        xStreamParser.useAttributeFor(IsoFileSystem.class, "volumeID");
        xStreamParser.useAttributeFor(IsoFileSystem.class, "applicationID");
        xStreamParser.useAttributeFor(IsoFileSystem.class, "isoLength");
        xStreamParser.useAttributeFor(IsoFileSystem.class, "publisherID");
        xStreamParser.useAttributeFor(IsoTreeNode.class, "file");
        xStreamParser.useAttributeFor(IsoTreeNode.class, "isRoot");
        xStreamParser.useAttributeFor(IsoTreeNode.class, "isoName");
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

    private void repairApplicationIDAndPublisherID(IsoFileSystem iso) {
        if (Objects.nonNull(iso)) {
            if (StringUtils.isBlank(iso.getPublisherID())) {
                iso.setPublisherID(UUID.randomUUID().toString());
            }
            if (!Strings.CI.equalsAny(MKISOFS_ISOFILESYSTEM_APPLICATIONID, iso.getApplicationID())) {
                iso.setApplicationID(MKISOFS_ISOFILESYSTEM_APPLICATIONID);
            }
        }
    }
}