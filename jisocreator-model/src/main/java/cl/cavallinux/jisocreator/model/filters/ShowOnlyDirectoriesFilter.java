package cl.cavallinux.jisocreator.model.filters;

import java.io.File;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filtro para mostrar solo los directorios
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.0.2
 * @since 0.0.2
 */
public class ShowOnlyDirectoriesFilter extends ViewerFilter {

    @Override
    public boolean select(Viewer arg0, Object arg1, Object arg2) {
        File file = (File) arg2;
        return file.isDirectory();
    }
}