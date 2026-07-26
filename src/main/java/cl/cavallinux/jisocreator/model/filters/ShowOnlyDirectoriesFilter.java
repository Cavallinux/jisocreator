package cl.cavallinux.jisocreator.model.filters;

import java.io.File;
import java.nio.file.Path;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * Filtro para mostrar solo los directorios
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.0.2
 * @since 0.0.2
 */
@Slf4j
@Builder
public class ShowOnlyDirectoriesFilter extends ViewerFilter {
    @Override
    public boolean select(Viewer arg0, Object arg1, Object arg2) {
        Path path = arg2 instanceof Path ? (Path) arg2 : ((File) arg2).toPath();
        OSExplorer osExplorer = OSAndIsoExplorerManager.INSTANCE.getOsExplorer();
        // Filesystem roots are handled separately from regular entries: rather than
        // relying on Files.isDirectory(Path) (which cannot distinguish "genuinely not
        // a directory" from "an I/O error occurred while checking", such as a fixed
        // disk blocked by an OS-level access restriction), isAccessibleRoot(Path)
        // only hides a root when it truly isn't browsable (e.g. a removable/optical
        // drive with no media), mirroring the same root short-circuit pattern already
        // used by ShowOnlyIsoDirectoriesFilter.
        boolean isDirectory = osExplorer.isRoot(path) ? osExplorer.isAccessibleRoot(path)
                : osExplorer.isDirectory(path);
        log.debug("Path: {}, isDirectory: {} ", path, isDirectory);
        return isDirectory;
    }
}