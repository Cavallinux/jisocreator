package cl.cavallinux.jisocreator.instances;

import org.eclipse.jface.preference.PreferenceStore;

import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.model.parser.IsoFilesystemParser;
import cl.cavallinux.jisocreator.model.parser.XMLIsoFilesystemParser;
import cl.cavallinux.jisocreator.util.IOUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IOManager {
    INSTANCE(IOUtils.builder()
                    .store(new PreferenceStore(IOUtils.JISOCREATOR_CONFIG_DIR
                            .concat(IOUtils.JISOCREATOR_CONFIG_FILENAME))).build(),
            XMLIsoFilesystemParser.builder().build());

    private IOUtils ioUtils;
    private IsoFilesystemParser<IsoFileSystem> isoFilesystemParser;
}
