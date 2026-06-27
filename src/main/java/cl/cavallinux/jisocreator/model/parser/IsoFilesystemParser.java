package cl.cavallinux.jisocreator.model.parser;

import java.util.Optional;
import java.util.UUID;

import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.util.IOUtils;

/**
 * Iso filesystems generic parser from file
 * 
 * @param <T> class which extends {@link IsoFileSystem}
 */
public interface IsoFilesystemParser<T extends IsoFileSystem> {
    final int MKISOFS_VOLUMEID_MAXLENGTH = 32;
    final String MKISOFS_ISOFILESYSTEM_APPLICATIONID = String.format("%s",
            IOUtils.class.getPackage().getImplementationTitle());
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
    
    default String generateInitialVolumeID() {
        String isoFileSystemVolumeID = UUID.randomUUID().toString();
        isoFileSystemVolumeID = isoFileSystemVolumeID.replace("-", "");
        return isoFileSystemVolumeID.substring(0, Math.min(isoFileSystemVolumeID.length(), MKISOFS_VOLUMEID_MAXLENGTH));
    }

    default String generateIsoFilesystemApplicationID() {
        return MKISOFS_ISOFILESYSTEM_APPLICATIONID;
    }
}
