package cl.cavallinux.jisocreator.model.providers.osexplorer;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.eclipse.jface.viewers.ITreeContentProvider;

import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class OSTreeContentProvider implements ITreeContentProvider {

    @Override
    public Object[] getElements(Object arg0) {
        return getChildren(arg0);
    }

    @Override
    public Object[] getChildren(Object arg0) {
        log.info("Arg received in getChildren: {}", arg0);
        if (arg0 instanceof File file) {
            return file.listFiles();
        } else {
            return OSAndIsoExplorerManager.INSTANCE.getOsExplorer().getRoots();
        }
    }

    @Override
    public Object getParent(Object arg0) {
        log.info("Arg received in getParent: {}", arg0);
        return ((File) arg0).getParentFile();
    }

    @Override
    public boolean hasChildren(Object arg0) {
        log.info("Arg received in hasChildren: {}", arg0);
        if (arg0 instanceof File file) {
            return directoryHasAnyEntry(file.toPath());
        } else {
            /** If arg0 is not a File (e.g., initial input is File[]), check if roots exist */
            File[] roots = OSAndIsoExplorerManager.INSTANCE.getOsExplorer().getRoots();
            return Objects.nonNull(roots) && roots.length > 0;
        }
    }

    /**
     * Checks whether the given directory contains at least one entry, without
     * reading the full directory listing.
     * <p>
     * Unlike {@link File#listFiles()} (used by {@link #getChildren(Object)}),
     * which eagerly materializes an array with every entry of the directory,
     * {@link Files#newDirectoryStream(Path)} lazily reads directory entries on
     * demand. Calling {@code iterator().hasNext()} therefore stops as soon as a
     * single entry is found, avoiding an unnecessary full directory scan just to
     * decide whether the tree node should be expandable. This is significantly
     * faster for directories containing a large number of files/subfolders.
     * </p>
     *
     * @param path the directory to check
     * @return true if the directory contains at least one entry, false otherwise
     */
    private boolean directoryHasAnyEntry(Path path) {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            return directoryStream.iterator().hasNext();
        } catch (IOException e) {
            log.warn("Error checking if directory has children for path: {}", path, e);
            return false;
        }
    }
}