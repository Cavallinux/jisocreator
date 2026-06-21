package cl.cavallinux.jisocreator.instances;

import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.model.parser.IsoFilesystemParser;
import cl.cavallinux.jisocreator.model.parser.XMLIsoFilesystemParser;
import cl.cavallinux.jisocreator.util.IOUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IOManager {
    INSTANCE(new IOUtils(), XMLIsoFilesystemParser.builder().build());

    private IOUtils ioUtils;
    private IsoFilesystemParser<IsoFileSystem> isoFilesystemParser;
}
