package cl.cavallinux.jisocreator.model.providers.decl;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Adapter class for providing labels and images to JFace Tree viewers.
 * <p>
 * This class extends {@link LabelProvider} and is responsible for supplying
 * text labels and images for tree elements in a tree viewer.
 * </p>
 * <p>
 * Subclasses should override the following methods to provide actual implementation:
 * <ul>
 *   <li>{@link #getText(Object)} - Provide text label for tree elements</li>
 *   <li>{@link #getImage(Object)} - Provide images for tree elements (optional)</li>
 *   <li>{@link #dispose()} - Clean up resources</li>
 * </ul>
 * </p>
 * 
 * @see LabelProvider
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.0.3
 * @since 0.0.3
 */
public class TreeLabelAdapter extends LabelProvider {

    /**
     * Provides a text label for the given tree element.
     * <p>
     * Subclasses should override this method to return meaningful text
     * representations of tree elements.
     * </p>
     * 
     * @param element the tree element
     * @return the text label to display, or null if no text should be shown
     */
    @Override
    public String getText(Object element) {
        return super.getText(element);
    }

    /**
     * Provides an image for the given tree element.
     * <p>
     * Subclasses can override this method to display icons or images
     * alongside tree element labels.
     * </p>
     * 
     * @param element the tree element
     * @return an image to display, or null if no image should be shown
     */
    @Override
    public Image getImage(Object element) {
        return super.getImage(element);
    }

    /**
     * Disposes of any resources held by this label provider.
     * <p>
     * Called when the viewer is disposed. Subclasses should override this
     * to release any resources, such as images or cached data.
     * </p>
     */
    @Override
    public void dispose() {
        super.dispose();
    }

}
