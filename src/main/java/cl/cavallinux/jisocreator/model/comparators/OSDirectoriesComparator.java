package cl.cavallinux.jisocreator.model.comparators;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import lombok.Builder;

/**
 * Comparator para directorios nativos del sistema operativo
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.0.2
 * @since 0.0.2
 */
public class OSDirectoriesComparator extends ViewerComparator {

    /**
     * Per-sort cache of computed categories, keyed by element identity. {@link
     * ViewerComparator#sort(Viewer, Object[])} performs an O(n log n) sort whose
     * comparisons each call {@link #category(Object)} twice, which would otherwise
     * repeat the underlying directory check for the same element multiple times.
     * Populating this cache once per {@link #sort(Viewer, Object[])} invocation
     * reduces the number of filesystem checks from O(n log n) to O(n) for large
     * directory listings. It is safe without synchronization because JFace viewer
     * sorting always happens on the single SWT UI thread.
     */
    private Map<Object, Integer> categoryCache;

    @Builder
    public OSDirectoriesComparator() {
        super();
    }

    @Override
    public void sort(Viewer viewer, Object[] elements) {
        categoryCache = new IdentityHashMap<>();
        try {
            super.sort(viewer, elements);
        } finally {
            categoryCache = null;
        }
    }

    @Override
    public int category(Object element) {
        return categoryCache != null ? categoryCache.computeIfAbsent(element, this::computeCategory)
                : computeCategory(element);
    }

    private int computeCategory(Object element) {
        File file = (File) element;
        Path path = file.toPath();
        // Delegates to OSExplorer#isDirectory, which consults the attribute cache
        // populated by warmAttributesCache(Path) (see LoadOSDirectoryContentsTask)
        // instead of always issuing a fresh Files.isDirectory(Path) stat call.
        boolean isDirectory = OSAndIsoExplorerManager.INSTANCE.getOsExplorer().isDirectory(path);
        BigInteger categoryResponse = isDirectory ? BigInteger.ZERO : BigInteger.ONE;
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