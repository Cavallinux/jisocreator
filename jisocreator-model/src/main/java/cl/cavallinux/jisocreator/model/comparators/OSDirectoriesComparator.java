package cl.cavallinux.jisocreator.model.comparators;

import java.io.File;

import org.eclipse.jface.viewers.ViewerComparator;

public class OSDirectoriesComparator extends ViewerComparator {
    @Override
    public int category(Object element) {
        return ((File) element).isDirectory() ? 0 : 1;
    }
}
