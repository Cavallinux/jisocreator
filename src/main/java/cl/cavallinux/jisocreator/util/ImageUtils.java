package cl.cavallinux.jisocreator.util;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;

public class ImageUtils {
    private ImageRegistry imageRegistry;
    private static final String IMAGES_FOLDER_PREFIX = "res/img/";
    private static final String DRIVE_IMAGE_FILENAME = "drive.png";
    private static final String FOLDER_IMAGE_FILENAME = "folder.png";
    private static final String ROOT_ISO_FILENAME = "iso.png";
    private static final String GENERIC_FILENAME = "file.png";

    public ImageUtils() {
        imageRegistry = new ImageRegistry(Display.getDefault());
    }

    public Image loadImage(Program program) {
        Image image = (Image) imageRegistry.get(program.getName());
        if (image == null) {
            image = Objects.nonNull(program.getImageData()) ? new Image(Display.getCurrent(), program.getImageData())
                    : loadImage(GENERIC_FILENAME);
            imageRegistry.put(program.getName(), image);
        }
        return image;
    }

    public Image loadImage(String name) {
        Image image = (Image) imageRegistry.get(name);
        if (image == null) {
            InputStream stream = getClass().getResourceAsStream(IMAGES_FOLDER_PREFIX.concat(name));
            image = new Image(Display.getDefault(), stream);
            imageRegistry.put(name, image);
        }
        return image;
    }

    /**
     * Loads an image based on the type of the given file system element.
     * <p>
     * This method accepts an {@link ITreeNode} and will resolve its underlying
     * element to either a {@link Path} or a {@link File} and delegate to the
     * {@link #loadImage(Path)} implementation. This avoids relying on deprecated
     * File-based APIs and supports both node implementations that expose File or
     * Path objects as their element.
     * </p>
     * 
     * @param node the tree node whose element determines the image
     * @return an Image object representing the node's element type
     */
    public Image loadImage(ITreeNode node) {
        if (node.isRoot()) {
            return loadImage(ROOT_ISO_FILENAME);
        }

        Object element = node.getElement();
        if (element == null) {
            return loadImage(GENERIC_FILENAME);
        }

        // Support both Path and File-backed nodes
        if (element instanceof Path) {
            return loadImage((Path) element);
        } else if (element instanceof File) {
            return loadImage(((File) element).toPath());
        } else {
            return loadImage(GENERIC_FILENAME);
        }
    }
    
    public Image loadImage(Path path) {
        if (OSAndIsoExplorerManager.INSTANCE.getOsExplorer().isRoot(path)) {
            return loadImage(DRIVE_IMAGE_FILENAME);
        } else if (Files.isDirectory(path)) {
            return loadImage(FOLDER_IMAGE_FILENAME);
        } else {
            String extension = OSAndIsoExplorerManager.INSTANCE.getOsExplorer().getExtension(path);
            if (StringUtils.isNotBlank(extension)) {
                Program program = Program.findProgram(extension);
                return Objects.nonNull(program) ? loadImage(program) : loadImage(GENERIC_FILENAME);
            } else {
                return loadImage(GENERIC_FILENAME);   
            }
        }
    }

    

    public ImageDescriptor loadImageDescriptor(String imagePath) {
        return ImageDescriptor.createFromImage(loadImage(imagePath));
    }
}