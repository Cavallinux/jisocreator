package cl.cavallinux.jisocreator.model.parser;

import java.util.Optional;

import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Builder
@Getter
@Slf4j
public class FallbackIsoFilesystemParser implements IsoFilesystemParser<IsoFileSystem> {
    private final IsoFilesystemParser<IsoFileSystem> primary;
    private final IsoFilesystemParser<IsoFileSystem> fallback;
    @Default
    private final boolean fallbackEnabled = true;

    @Override
    public Optional<IsoFileSystem> deserialize(String filePath) {
        Optional<IsoFileSystem> primaryResult = primary.deserialize(filePath);
        if (primaryResult.isPresent()) {
            return primaryResult;
        }
        if (!fallbackEnabled) {
            log.warn("Primary XML parser could not deserialize {} and fallback is disabled", filePath);
            return Optional.empty();
        }
        log.warn("Primary XML parser could not deserialize {}, trying fallback parser", filePath);
        return fallback.deserialize(filePath);
    }

    @Override
    public boolean serialize(IsoFileSystem isoFilesystem, String filePath) {
        boolean primarySerialized = primary.serialize(isoFilesystem, filePath);
        if (primarySerialized) {
            return true;
        }
        if (!fallbackEnabled) {
            log.warn("Primary XML parser could not serialize {} and fallback is disabled", filePath);
            return false;
        }
        log.warn("Primary XML parser could not serialize {}, trying fallback parser", filePath);
        return fallback.serialize(isoFilesystem, filePath);
    }
}
