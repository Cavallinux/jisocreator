package cl.cavallinux.jisocreator.model.providers.impl.osexplorer;

import java.io.File;

import org.eclipse.swt.graphics.Image;

import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;
import cl.cavallinux.jisocreator.model.providers.decl.TableProviderAdapter;
import cl.cavallinux.jisocreator.util.ImageUtils;

public class OsTableProvider extends TableProviderAdapter {
    private static Object[] EMPTY = new Object[0];

    @Override
    public Object[] getElements(Object arg0) {
	File[] files = ((File) arg0).listFiles();
	if (files == null) {
	    return EMPTY;
	} else {
	    return files;
	}
    }

    @Override
    public Image getColumnImage(Object arg0, int arg1) {
	switch (arg1) {
	case 0:
	    return ImageUtils.getInstance().loadImage((File) arg0);
	default:
	    return null;
	}
    }

    @Override
    public String getColumnText(Object arg0, int arg1) {
	File file = ((File) arg0);
	switch (arg1) {
	case 0:
	    return OSExplorer.getInstance().getName(file);
	case 1:
	    return OSExplorer.getInstance().length(file);
	case 2:
	    return OSExplorer.getInstance().getFileType(file);
	case 3:
	    return OSExplorer.getInstance().lastModified(file);
	default:
	    return null;
	}
    }
}