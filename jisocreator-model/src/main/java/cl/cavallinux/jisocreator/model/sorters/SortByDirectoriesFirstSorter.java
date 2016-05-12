package cl.cavallinux.jisocreator.model.sorters;

import java.io.File;

import org.eclipse.jface.viewers.ViewerSorter;

public class SortByDirectoriesFirstSorter extends ViewerSorter {
    @Override
    public int category(Object element) {
	return ((File) element).isDirectory() ? 0 : 1;
    }
}
