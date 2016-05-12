package cl.cavallinux.jisocreator.model.filters.isoexplorer;

import java.io.File;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;

public class ShowOnlyIsoDirectoriesFilter extends ViewerFilter {

    @Override
    public boolean select(Viewer arg0, Object arg1, Object arg2) {
	ITreeNode node = (ITreeNode) arg2;
	if (node.isRoot()) {
	    return true;
	} else {
	    File file = (File) node.getElement();
	    return file.isDirectory();
	}
    }
}
