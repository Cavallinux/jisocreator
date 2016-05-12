package cl.cavallinux.jisocreator.model.filters;

import java.io.File;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class ShowOnlyDirectoriesFilter extends ViewerFilter {

    @Override
    public boolean select(Viewer arg0, Object arg1, Object arg2) {
	return ((File) arg2).isDirectory();
    }
}
