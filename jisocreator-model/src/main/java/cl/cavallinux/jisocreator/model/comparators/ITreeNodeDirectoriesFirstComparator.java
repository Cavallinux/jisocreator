package cl.cavallinux.jisocreator.model.comparators;

import java.io.File;

import org.eclipse.jface.viewers.ViewerComparator;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;

public class ITreeNodeDirectoriesFirstComparator extends ViewerComparator {
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
