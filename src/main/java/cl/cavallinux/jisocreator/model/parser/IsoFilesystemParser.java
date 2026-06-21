package cl.cavallinux.jisocreator.model.parser;

import java.util.Optional;

import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;

/**
 * Iso filesystems generic parser from file
 * 
 * @param <T> class which extends {@link IsoFileSystem}
 */
public interface IsoFilesystemParser<T extends IsoFileSystem> {
    /**
     * Deserialize from path
     * 
     * @param filePath
     * @return an {@link Optional} with the deserialized iso filesystem
     */
    default Optional<T> deserialize(String filePath) {
        return Optional.empty();
    }

    /**
     * Serializes iso filesystem to file
     * 
     * @param isoFilesystem Filesystem to serialize
     * @param filePath
     */
    default boolean serialize(T isoFilesystem, String filePath) {
        return false;
    }
    
    default Object obtainParser() {
        return null;
    }
}
