package cl.cavallinux.jisocreator.model.providers.impl.osexplorer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.graphics.Image;

import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;
import cl.cavallinux.jisocreator.model.providers.decl.TableProviderAdapter;
import cl.cavallinux.jisocreator.util.ImageUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Table content and label provider for OS file system explorer.
 * <p>
 * This class provides table data and rendering for displaying files and
 * directories from the operating system file system. It extends
 * {@link TableProviderAdapter} to implement the content and label provider
 * functionality.
 * </p>
 * <p>
 * The table displays the following columns for each file:
 * <ol>
 * <li>File name with icon</li>
 * <li>File size</li>
 * <li>File type</li>
 * <li>Last modified date</li>
 * </ol>
 * </p>
 * <p>
 * The input to this provider should be a {@link File} object representing a
 * directory whose contents are to be displayed.
 * </p>
 * 
 * @see TableProviderAdapter
 * @see OSExplorer
 * @see ImageUtils
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.0.3
 * @since 0.0.3
 */
@Slf4j
public class OsTableProvider extends TableProviderAdapter {
    private static final Object[] EMPTY = new Object[0];

    /**
     * Retrieves the file list from a directory.
     * <p>
     * This method expects the input object to be a {@link File} representing a
     * directory. It returns an array of files and subdirectories within that
     * directory. If the file is not a directory or cannot be read, an empty array
     * is returned.
     * </p>
     * 
     * @param inputElement a File object representing a directory
     * @return an array of File objects in the directory, or an empty array if none
     */
    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof Path) {
            return getElements((Path) inputElement);
        } else {
            File[] files = ((File) inputElement).listFiles();
            return files != null ? files : EMPTY;
        }
    }
    
    public Object[] getElements(Path inputElement) {
        try (Stream<Path> stream = Files.list(inputElement)) {
            return stream.toArray();
        } catch (IOException e) {
            log.error("Error listing files in directory: {}", inputElement, e);
            return EMPTY;
        }
    }

    /**
     * Provides an icon image for a file in the table.
     * <p>
     * Column 0 displays the file's icon based on its type. Other columns do not
     * display images.
     * </p>
     * 
     * @param element     a File object
     * @param columnIndex the zero-based column index (0=icon, others=null)
     * @return the file's icon image, or null if no image should be displayed
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        Path path = element instanceof Path ? (Path) element : ((File) element).toPath();
        switch (columnIndex) {
        case 0:
            return ImageRegister.INSTANCE.getImageUtils().loadImage(path);
        default:
            return null;
        }
    }

    /**
     * Provides text content for file columns in the table.
     * <p>
     * The method retrieves file metadata through {@link OSExplorer}:
     * <ul>
     * <li>Column 0: File name</li>
     * <li>Column 1: File size</li>
     * <li>Column 2: File type</li>
     * <li>Column 3: Last modified date</li>
     * </ul>
     * </p>
     * 
     * @param element     a File object
     * @param columnIndex the zero-based column index
     * @return the file property as a string, or null if invalid column
     */
    //TODO refactor to cast from File to Path in getElements and pass Path to getColumnText, to avoid repeated casting and allow better separation of concerns
    @Override
    public String getColumnText(Object element, int columnIndex) {
        Path filePath = element instanceof Path ? (Path) element : ((File) element).toPath();
        OSExplorer explorer = OSAndIsoExplorerManager.INSTANCE.getOsExplorer();
        switch (columnIndex) {
        case 0:
            return explorer.getName(filePath);
        case 1:
            return explorer.getFileType(filePath);
        case 2:
            return explorer.length(filePath);
        case 3:
            return explorer.lastModified(filePath);
        default:
            return StringUtils.EMPTY;
        }
    }
}