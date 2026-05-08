package cl.cavallinux.jisocreator.model.comparators;

import java.io.File;
import java.math.BigInteger;

import org.eclipse.jface.viewers.ViewerComparator;

/**
 * Comparator para directorios nativos del sistema operativo
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.0.2
 * @since 0.0.2
 */
public class OSDirectoriesComparator extends ViewerComparator {
    @Override
    public int category(Object element) {
        File file = (File) element;
        return file.isDirectory() ? BigInteger.ZERO.intValue() : BigInteger.ONE.intValue();
    }
}