package cl.cavallinux.jisocreator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
    private final static IOUtils instance;
    private final static String JISOCREATOR_CONFIG_DIR;
    private final static String JISOCREATOR_DEFAULTCONFIG_FILENAME;
    private final static String JISOCREATOR_CONFIG_FILENAME;
    private PreferenceStore store;
    private Properties defaultProperties;
    private XStream xStreamParser;

    static {
        JISOCREATOR_CONFIG_DIR = System.getProperty("user.home").concat("/.config/jisocreator/");
        JISOCREATOR_DEFAULTCONFIG_FILENAME = "defaultconfig.properties";
        JISOCREATOR_CONFIG_FILENAME = "jisocreator.properties";
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
        try (FileOutputStream fos = new FileOutputStream(path)){
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
        InputStream stream = null;
        try {
            File file = new File(JISOCREATOR_CONFIG_DIR);
            file.mkdirs();
            stream = getClass().getResourceAsStream("res/conf/".concat(JISOCREATOR_DEFAULTCONFIG_FILENAME));
            defaultProperties = new Properties();
            defaultProperties.load(stream);
            store.setValue("mkisofs.rockridge.use",
                    Boolean.parseBoolean(defaultProperties.getProperty("mkisofs.rockridge.use")));
            store.setValue("mkisofs.joliet.use",
                    Boolean.parseBoolean(defaultProperties.getProperty("mkisofs.joliet.use")));
            store.setValue("mkisofs.symlinks.follow",
                    Boolean.parseBoolean(defaultProperties.getProperty("mkisofs.symlinks.follow")));
            if (SWT.getPlatform().equals("gtk"))
                store.setValue("mkisofs.path", defaultProperties.getProperty("mkisofs.unix.path"));
            else if (SWT.getPlatform().equals("win32"))
                store.setValue("mkisofs.path", defaultProperties.getProperty("mkisofs.win32.path"));
            store.save();
            stream.close();
        } catch (IOException e) {
            log.error("Error saving properties", e);
        }
    }
}