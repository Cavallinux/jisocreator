package cl.cavallinux.jisocreator.model.filters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * Filtro para mostrar u ocultar los archivos ocultos del sistema operativo
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.0.2
 * @since 0.0.2
 */
@Slf4j
public class HideHiddenFilesFilter extends ViewerFilter {
    // TODO cast directly element to Path
    @Override
    public boolean select(Viewer arg0, Object arg1, Object arg2) {
        try {
            File file = (File) arg2;
            return !Files.isHidden(file.toPath());
        } catch (IOException e) {
            log.error("Error selecting file: {}", e);
            return false;
        }
    }
}