package cl.cavallinux.jisocreator.model.sorters;

import java.io.File;

import org.eclipse.jface.viewers.ViewerSorter;

import cl.cavallinux.jisocreator.model.comparators.ITreeNodeDirectoriesFirstComparator;

/**
 * @deprecated Use instead {@link ITreeNodeDirectoriesFirstComparator}
 */
public class SortByDirectoriesFirstSorter extends ViewerSorter {
    @Override
    public int category(Object element) {
        return ((File) element).isDirectory() ? 0 : 1;
    }
}
