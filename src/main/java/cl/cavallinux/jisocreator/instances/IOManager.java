package cl.cavallinux.jisocreator.instances;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.PreferenceStore;

import cl.cavallinux.jisocreator.model.parser.FallbackIsoFilesystemParser;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.model.parser.IsoFilesystemParser;
import cl.cavallinux.jisocreator.model.parser.LegacyXStreamIsoFilesystemParser;
import cl.cavallinux.jisocreator.model.parser.XMLIsoFilesystemParser;
import cl.cavallinux.jisocreator.util.IOUtils;
import lombok.Getter;

@Getter
public enum IOManager {
    INSTANCE(obtainIOUtils());

    private static final String XML_FALLBACK_SYSTEM_PROPERTY = "jisocreator.xml.fallback.enabled";
    private static final String XML_FALLBACK_PREFERENCE_KEY = "xml.parser.fallback.enabled";

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
        return FallbackIsoFilesystemParser.builder()
                .primary(XMLIsoFilesystemParser.builder().build())
                .fallback(LegacyXStreamIsoFilesystemParser.builder().build())
                .fallbackEnabled(isFallbackEnabled(ioUtils))
                .build();
    }

    private static boolean isFallbackEnabled(IOUtils ioUtils) {
        String configuredFallback = System.getProperty(XML_FALLBACK_SYSTEM_PROPERTY);
        if (StringUtils.isNotBlank(configuredFallback)) {
            return Boolean.parseBoolean(configuredFallback);
        }
        return ioUtils.getStore().getBoolean(XML_FALLBACK_PREFERENCE_KEY);
    }
}
