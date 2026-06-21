package cl.cavallinux.jisocreator.model.parser;

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
    public ObjectMapper obtainObjectMapper() {
        return new XmlMapper();
    }
}