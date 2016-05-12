package cl.cavallinux.jisocreator.model.providers.impl.isoexplorer;

import java.io.File;

import org.eclipse.swt.graphics.Image;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;
import cl.cavallinux.jisocreator.model.providers.decl.TableProviderAdapter;

public class IsoTableProvider extends TableProviderAdapter {
    @Override
    public Object[] getElements(Object arg0) {
	return ((ITreeNode) arg0).getChildren();
    }

    @Override
    public Image getColumnImage(Object arg0, int arg1) {
	switch (arg1) {
	case 0:
	    return ((ITreeNode) arg0).getImage();
	default:
	    return null;
	}
    }

    @Override
    public String getColumnText(Object arg0, int arg1) {
	File file = (File) ((ITreeNode) arg0).getElement();
	switch (arg1) {
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