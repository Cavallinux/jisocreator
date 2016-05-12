package cl.cavallinux.jisocreator.model.providers.impl.osexplorer;

import java.io.File;

import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;
import cl.cavallinux.jisocreator.model.providers.decl.TreeContentAdapter;

public class OSTreeContentProvider extends TreeContentAdapter {

    @Override
    public Object[] getElements(Object arg0) {
	return getChildren(arg0);
    }

    @Override
    public Object[] getChildren(Object arg0) {
	if (arg0 instanceof OSExplorer) {
	    return ((OSExplorer) arg0).getRoots();
	} else {
	    return ((File) arg0).listFiles();
	}
    }

    @Override
    public Object getParent(Object arg0) {
	return ((File) arg0).getParentFile();
    }

    @Override
    public boolean hasChildren(Object arg0) {
	return ((File) arg0).listFiles() != null;
    }
}