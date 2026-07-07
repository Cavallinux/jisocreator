package cl.cavallinux.jisocreator.model.comparators;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import lombok.Builder;

/**
 * Comparator para directorios nativos del sistema operativo
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.0.2
 * @since 0.0.2
 */
@Builder
public class OSDirectoriesComparator extends ViewerComparator {
    @Override
    public int category(Object element) {
        File file = (File) element;
        Path path = file.toPath();
        BigInteger categoryResponse = Files.isDirectory(path) ? BigInteger.ZERO : BigInteger.ONE;
        return categoryResponse.intValue();
    }
    
    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        int categoryDiff = category(e1) - category(e2);
        return categoryDiff != 0 ? categoryDiff : compareFiles(e1, e2);
    }

    private int compareFiles(Object e1, Object e2) {
        File file1 = (File) e1;
        Path path1 = file1.toPath();
        File file2 = (File) e2;
        Path path2 = file2.toPath();
        return path1.compareTo(path2);
    }
}