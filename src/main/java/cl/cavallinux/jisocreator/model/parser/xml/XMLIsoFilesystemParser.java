package cl.cavallinux.jisocreator.model.parser.xml;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.model.parser.decl.IsoFilesystemParser;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Builder
@Getter
@Slf4j
public class XMLIsoFilesystemParser implements IsoFilesystemParser<IsoFileSystem> {
    @Builder.Default
    private final XmlMapper parser = obtainParser();
    
    @Override
    public Optional<IsoFileSystem> deserialize(String filePath) {
        try (InputStream fis = new FileInputStream(filePath)) {
            XMLIsoFilesystemContract.Iso9660Document document = parser.readValue(fis,
                    XMLIsoFilesystemContract.Iso9660Document.class);
            IsoFileSystem iso = XMLIsoFilesystemContractMapper.toIsoFilesystem(document);
            repairApplicationIDAndPublisherID(iso);
            return Optional.ofNullable(iso);
        } catch (IOException e) {
            log.error("Error parsing XML", e);
            return IsoFilesystemParser.super.deserialize(filePath);
        }
    }
    
    @Override
    public boolean serialize(IsoFileSystem isoFilesystem, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            XMLIsoFilesystemContract.Iso9660Document document = XMLIsoFilesystemContractMapper.toDocument(isoFilesystem);
            parser.writerWithDefaultPrettyPrinter().writeValue(fos, document);
            return true;
        } catch (IOException e) {
            log.error("Error saving XML", e);
            return false;
        }
    }
    
    private void repairApplicationIDAndPublisherID(IsoFileSystem iso) {
        if (Objects.nonNull(iso)) {
            String requiredApplicationID = generateIsoFilesystemApplicationID();
            if (StringUtils.isBlank(iso.getPublisherID())) {
                iso.setPublisherID(UUID.randomUUID().toString());
            }
            if (!Strings.CI.equalsAny(requiredApplicationID, iso.getApplicationID())) {
                iso.setApplicationID(requiredApplicationID);
            }
        }
    }
    
    private static XmlMapper obtainParser() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return xmlMapper;
    }
}