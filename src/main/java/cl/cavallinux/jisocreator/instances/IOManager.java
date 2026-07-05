package cl.cavallinux.jisocreator.instances;

import org.eclipse.jface.preference.PreferenceStore;

import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.model.parser.IsoFilesystemParser;
import cl.cavallinux.jisocreator.model.parser.XMLIsoFilesystemParser;
import cl.cavallinux.jisocreator.util.IOUtils;
import lombok.Getter;

@Getter
public enum IOManager {
    INSTANCE(obtainIOUtils());

    private final IOUtils ioUtils;
    private final IsoFilesystemParser<IsoFileSystem> isoFilesystemParser;

    IOManager(IOUtils ioUtils) {
        this.ioUtils = ioUtils;
        this.isoFilesystemParser = obtainIsoFilesystemParser(ioUtils);
    }

    private static IOUtils obtainIOUtils() {
        return IOUtils.builder()
                .store(new PreferenceStore(IOUtils.JISOCREATOR_CONFIG_DIR
                        .concat(IOUtils.JISOCREATOR_CONFIG_FILENAME)))
                .build();
    }

    private static IsoFilesystemParser<IsoFileSystem> obtainIsoFilesystemParser(IOUtils ioUtils) {
        return XMLIsoFilesystemParser.builder().build();
    }
}
