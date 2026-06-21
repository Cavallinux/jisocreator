package cl.cavallinux.jisocreator.model.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Builder
@Getter
@Slf4j
@AllArgsConstructor
public class XMLIsoFilesystemParser implements IsoFilesystemParser<IsoFileSystem> {
    @Override
    public Optional<IsoFileSystem> deserialize(String filePath) {
        try (InputStream stream = new FileInputStream(filePath)) {
            ObjectMapper objectMapper = obtainObjectMapper();
            IsoFileSystem isoFileSystem = objectMapper.readValue(filePath, IsoFileSystem.class);
            return Optional.of(isoFileSystem);
        } catch (IOException e) {
            log.error("Error loading xml file", e);
            return IsoFilesystemParser.super.deserialize(filePath);
        }
    }
    
    @Override
    public ObjectMapper obtainObjectMapper() {
        return new XmlMapper();
    }
}