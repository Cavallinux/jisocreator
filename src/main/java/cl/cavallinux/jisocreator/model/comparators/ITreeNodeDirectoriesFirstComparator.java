package cl.cavallinux.jisocreator.model.comparators;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;

/**
 * Comparator para ordenar primero por directorio raiz y luego por archivo o
 * directorio dentro del mismo archivo ISO
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.0.2
 * @since 0.0.2
 */
public class ITreeNodeDirectoriesFirstComparator extends ViewerComparator {
    @Override
    public int category(Object element) {
        ITreeNode node = (ITreeNode) element;
        return node.isRoot() ? BigInteger.ZERO.intValue() : category(node);
    }

    private int category(ITreeNode element) {
        File file = (File) element.getElement();
        Path path = file.toPath();
        BigInteger compareResult = Files.isDirectory(path) ? BigInteger.ZERO : BigInteger.ONE;
        return compareResult.intValue();
    }
    
    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        int categoryDiff = category(e1) - category(e2);
        return categoryDiff != 0 ? categoryDiff : compareFiles(e1, e2);
    }

    private int compareFiles(Object e1, Object e2) {
        File file1 = (File) ((ITreeNode) e1).getElement();
        Path path1 = file1.toPath();
        File file2 = (File) ((ITreeNode) e2).getElement();
        Path path2 = file2.toPath();
        return path1.compareTo(path2);
    }
}