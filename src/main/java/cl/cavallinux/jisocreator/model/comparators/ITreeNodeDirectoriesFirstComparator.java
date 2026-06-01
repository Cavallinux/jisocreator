package cl.cavallinux.jisocreator.model.comparators;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;

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
        return Files.isDirectory(path) ? BigInteger.ZERO.intValue() : BigInteger.ONE.intValue();
    }
}