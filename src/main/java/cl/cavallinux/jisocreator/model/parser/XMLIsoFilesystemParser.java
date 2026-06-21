package cl.cavallinux.jisocreator.model.parser;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoTreeNode;
import cl.cavallinux.jisocreator.util.IOUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Builder
@Getter
@Slf4j
@AllArgsConstructor
public class XMLIsoFilesystemParser implements IsoFilesystemParser<IsoFileSystem> {
    private final static String MKISOFS_ISOFILESYSTEM_APPLICATIONID;
    
    static {
        MKISOFS_ISOFILESYSTEM_APPLICATIONID = String.format("%s", IOUtils.class.getPackage().getImplementationTitle());
    }
    
    @Override
    public Optional<IsoFileSystem> deserialize(String filePath) {
        try (InputStream fis = new FileInputStream(filePath)) {
            IsoFileSystem iso = (IsoFileSystem) ((XStream) obtainParser()).fromXML(fis);
            repairApplicationIDAndPublisherID(iso);
            return Optional.of(iso);
        } catch (IOException | XStreamException e) {
            log.error("Error parsing XML", e);
            return IsoFilesystemParser.super.deserialize(filePath);
        }
    }
    
    @Override
    public boolean serialize(IsoFileSystem isoFilesystem, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            ((XStream) obtainParser()).toXML(isoFilesystem, fos);
            return true;
        } catch (IOException e) {
            log.error("Error saving XML", e);
            return false;
        }
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
    
    @Override
    public Object obtainParser() {
        XStream xStreamParser = new XStream(new PureJavaReflectionProvider());
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

        return xStreamParser;
    }
}