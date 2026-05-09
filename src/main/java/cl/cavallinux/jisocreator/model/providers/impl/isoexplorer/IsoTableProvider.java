package cl.cavallinux.jisocreator.model.providers.impl.isoexplorer;

import java.io.File;

import org.eclipse.swt.graphics.Image;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;
import cl.cavallinux.jisocreator.model.providers.decl.TableProviderAdapter;

/**
 * Table content and label provider for ISO file system explorer.
 * <p>
 * This class provides table data and rendering for displaying files and directories
 * from an ISO 9660 image. It extends {@link TableProviderAdapter} to implement
 * the content and label provider functionality.
 * </p>
 * <p>
 * The table displays the following columns for each ISO entry:
 * <ol>
 *   <li>File name with icon</li>
 *   <li>File size</li>
 *   <li>File type</li>
 *   <li>Last modified date</li>
 *   <li>Absolute path</li>
 * </ol>
 * </p>
 * <p>
 * The input to this provider should be an {@link ITreeNode} object representing
 * a directory entry within an ISO image whose contents are to be displayed.
 * </p>
 * 
 * @see TableProviderAdapter
 * @see ITreeNode
 * @see OSExplorer
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.0.3
 * @since 0.0.3
 */
public class IsoTableProvider extends TableProviderAdapter {
    
    /**
     * Retrieves the child entries from an ISO directory node.
     * <p>
     * This method expects the input object to be an {@link ITreeNode} representing
     * a directory entry in an ISO image. It returns an array of child entries
     * within that directory.
     * </p>
     * 
     * @param inputElement an ITreeNode object representing an ISO directory entry
     * @return an array of child ITreeNode objects, or an empty array if none
     */
    @Override
    public Object[] getElements(Object inputElement) {
        return ((ITreeNode) inputElement).getChildren();
    }

    /**
     * Provides an icon image for an ISO entry in the table.
     * <p>
     * Column 0 displays the entry's icon based on its type (file or directory).
     * Other columns do not display images.
     * </p>
     * 
     * @param element an ITreeNode object representing an ISO entry
     * @param columnIndex the zero-based column index (0=icon, others=null)
     * @return the entry's icon image, or null if no image should be displayed
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        switch (columnIndex) {
        case 0:
            return ((ITreeNode) element).getImage();
        default:
            return null;
        }
    }

    /**
     * Provides text content for ISO entry columns in the table.
     * <p>
     * The method retrieves file metadata from the underlying {@link File} object
     * wrapped by the {@link ITreeNode} through {@link OSExplorer}:
     * <ul>
     *   <li>Column 0: File name</li>
     *   <li>Column 1: File size</li>
     *   <li>Column 2: File type</li>
     *   <li>Column 3: Last modified date</li>
     *   <li>Column 4: Absolute path</li>
     * </ul>
     * </p>
     * 
     * @param element an ITreeNode object representing an ISO entry
     * @param columnIndex the zero-based column index
     * @return the entry property as a string, or null if invalid column
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
        File file = (File) ((ITreeNode) element).getElement();
        switch (columnIndex) {
        case 0:
            return OSExplorer.getInstance().getName(file);
        case 1:
            return OSExplorer.getInstance().length(file);
        case 2:
            return OSExplorer.getInstance().getFileType(file);
        case 3:
            return OSExplorer.getInstance().lastModified(file);
        case 4:
            return OSExplorer.getInstance().getAbsolutePath(file);
        default:
            return null;
        }
    }
}