package cl.cavallinux.jisocreator.model.providers.impl.osexplorer;

import java.io.File;

import org.eclipse.swt.graphics.Image;

import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;
import cl.cavallinux.jisocreator.model.providers.decl.TreeLabelAdapter;
import cl.cavallinux.jisocreator.util.ImageUtils;

public class OSTreeLabelProvider extends TreeLabelAdapter {

    @Override
    public Image getImage(Object element) {
	return ImageUtils.getInstance().loadImage((File) element);
    }

    @Override
    public String getText(Object element) {
	if (OSExplorer.getInstance().isRoot((File) element)) {
	    return ((File) element).getAbsolutePath();
	} else {
	    return ((File) element).getName();
	}
    }
}