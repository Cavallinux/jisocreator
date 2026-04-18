package cl.cavallinux.jisocreator.util;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;

public class ImageUtils {
    private static ImageUtils instance;
    private ImageRegistry imageRegistry;
    private static final String IMAGES_FOLDER_PREFIX = "res/img/";
    private static final String DRIVE_IMAGE_FILENAME = "drive.png";
    private static final String FOLDER_IMAGE_FILENAME = "folder.png";
    private static final String ROOT_ISO_FILENAME = "iso.png";
    private static final String GENERIC_FILENAME = "file.png";

    private ImageUtils() {
        imageRegistry = new ImageRegistry(Display.getDefault());
    }

    public static ImageUtils getInstance() {
        return Objects.nonNull(instance) ? instance : newInstance();
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

    public Image loadImage(File file) {
        if (OSExplorer.getInstance().isRoot(file)) {
            return loadImage(DRIVE_IMAGE_FILENAME);
        } else if (file.isDirectory()) {
            return loadImage(FOLDER_IMAGE_FILENAME);
        } else {
            String extension = OSExplorer.getInstance().getExtension(file);
            if (extension.equals("")) {
                return loadImage(GENERIC_FILENAME);
            } else {
                Program program = Program.findProgram(extension);
                if (program == null) {
                    return loadImage(GENERIC_FILENAME);
                } else {
                    return loadImage(program);
                }
            }
        }
    }

    public Image loadImage(ITreeNode node) {
        if (node.isRoot()) {
            return loadImage(ROOT_ISO_FILENAME);
        } else {
            return loadImage((File) node.getElement());
        }
    }

    public ImageDescriptor loadImageDescriptor(String imagePath) {
        return ImageDescriptor.createFromImage(loadImage(imagePath));
    }
    
    private static ImageUtils newInstance() {
        instance = new ImageUtils();
        return instance;
    }
}