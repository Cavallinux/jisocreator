package cl.cavallinux.jisocreator.model.filters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * Filtro para mostrar u ocultar los archivos ocultos del sistema operativo
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.0.2
 * @since 0.0.2
 */
@Slf4j
@Builder
public class HideHiddenFilesFilter extends ViewerFilter {
    @Override
    public boolean select(Viewer arg0, Object arg1, Object arg2) {
        try {
            File file = (File) arg2;
            return !Files.isHidden(file.toPath());
        } catch (IOException e) {
            // Allow the element on IOException (e.g., access denied for system drives or root paths in Windows)
            // This prevents system root drives from being filtered out due to permission issues
            log.warn("Error checking if file is hidden for path: {}, allowing element to be displayed. Error: {}", 
                    ((File) arg2).getAbsolutePath(), e.getMessage());
            return true;
        }
    }
}