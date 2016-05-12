package cl.cavallinux.jisocreator.model.sorters;

import java.io.File;

import org.eclipse.jface.viewers.ViewerSorter;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;

public class SortByIsoEntryFirstSorter extends ViewerSorter {
    @Override
    public int category(Object element) {
	ITreeNode node = (ITreeNode) element;
	if (node.isRoot()) {
	    return 0;
	} else {
	    File file = (File) node.getElement();
	    return file.isDirectory() ? 0 : 1;
	}
    }
}
