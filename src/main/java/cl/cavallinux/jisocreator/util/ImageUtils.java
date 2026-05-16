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

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;

public class ImageUtils {
    private ImageRegistry imageRegistry;
    private static final String IMAGES_FOLDER_PREFIX = "res/img/";
    private static final String DRIVE_IMAGE_FILENAME = "drive.png";
    private static final String FOLDER_IMAGE_FILENAME = "folder.png";
    private static final String ROOT_ISO_FILENAME = "iso.png";
    private static final String GENERIC_FILENAME = "file.png";
    private static final ImageUtils instance;
    
    static {
        instance = new ImageUtils();
    }

    private ImageUtils() {
        imageRegistry = new ImageRegistry(Display.getDefault());
    }

    public static ImageUtils getInstance() {
        return instance;
    }

    public Image loadImage(Program program) {
        Image image = (Image) imageRegistry.get(program.getName());
        if (image == null) {
            image = new Image(Display.getCurrent(), program.getImageData());
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
     * Loads an image based on the type of the given file.
     * <p>
     * This method determines the appropriate image to load based on the
     * characteristics of the provided file. It checks if the file is a root
     * directory, a regular directory, or a regular file, and loads the
     * corresponding image accordingly.
     * </p>
     * 
     * @param file the File object for which to load an image
     * @return an Image object representing the file type
     * @deprecated Use {@link #loadImage(Path)} instead for better abstraction and
     *             flexibility.
     */
    @Deprecated
    public Image loadImage(File file) {
        if (OSExplorer.getInstance().isRoot(file)) {
            return loadImage(DRIVE_IMAGE_FILENAME);
        } else if (file.isDirectory()) {
            return loadImage(FOLDER_IMAGE_FILENAME);
        } else {
            String extension = OSExplorer.getInstance().getExtension(file);
            if (StringUtils.isBlank(extension)) {
                Program program = Program.findProgram(extension);
                return Objects.nonNull(program) ? loadImage(program) : loadImage(GENERIC_FILENAME);
            } else {
                return loadImage(GENERIC_FILENAME);   
            }
        }
    }
    
    public Image loadImage(Path path) {
        if (OSExplorer.getInstance().isRoot(path)) {
            return loadImage(DRIVE_IMAGE_FILENAME);
        } else if (Files.isDirectory(path)) {
            return loadImage(FOLDER_IMAGE_FILENAME);
        } else {
            String extension = OSExplorer.getInstance().getExtension(path);
            if (StringUtils.isBlank(extension)) {
                Program program = Program.findProgram(extension);
                return Objects.nonNull(program) ? loadImage(program) : loadImage(GENERIC_FILENAME);
            } else {
                return loadImage(GENERIC_FILENAME);   
            }
        }
    }

    public Image loadImage(ITreeNode node) {
        return node.isRoot() ? loadImage(ROOT_ISO_FILENAME) : loadImage((File) node.getElement());
    }

    public ImageDescriptor loadImageDescriptor(String imagePath) {
        return ImageDescriptor.createFromImage(loadImage(imagePath));
    }
}