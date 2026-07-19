package cl.cavallinux.jisocreator.model.filters;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

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
        boolean isDirectory = Files.isDirectory(path);
        log.debug("Path: {}, isDirectory: {} ", path, isDirectory);
        return isDirectory;
    }
}