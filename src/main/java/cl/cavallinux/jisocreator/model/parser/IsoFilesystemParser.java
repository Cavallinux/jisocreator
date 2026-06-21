package cl.cavallinux.jisocreator.model.parser;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    default void serialize(T isoFilesystem, String filePath) {
        
    }

    /**
     * Obtain the object mapper
     * 
     * @return the {@link ObjectMapper}
     */
    default ObjectMapper obtainObjectMapper() {
        return new ObjectMapper();
    }
}
