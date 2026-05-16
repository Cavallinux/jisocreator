package cl.cavallinux.jisocreator.model.providers.impl.osexplorer;

import java.io.File;
import java.nio.file.Path;

import org.eclipse.swt.graphics.Image;

import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;
import cl.cavallinux.jisocreator.model.providers.decl.TreeLabelAdapter;
import cl.cavallinux.jisocreator.util.ImageUtils;

public class OSTreeLabelProvider extends TreeLabelAdapter {

    @Override
    public Image getImage(Object element) {
        return ImageUtils.getInstance().loadImage(((File) element).toPath());
    }

    //TODO cast directly element from File to Path
    @Override
    public String getText(Object element) {
        OSExplorer instance = OSExplorer.getInstance();
        Path path = element instanceof Path ? (Path) element : ((File) element).toPath();
        return instance.getName(path);
    }
}