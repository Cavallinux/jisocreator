package cl.cavallinux.jisocreator.model.providers.impl.isoexplorer;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import org.eclipse.jface.viewers.ITreeContentProvider;

public class IsoTreeContentProvider implements ITreeContentProvider {
    @Override
    public Object[] getElements(Object arg0) {
        return getChildren(arg0);
    }

    @Override
    public Object[] getChildren(Object arg0) {
        if (arg0 instanceof IsoFileSystem) {
            return ((IsoFileSystem) arg0).toArray();
        } else {
            return ((ITreeNode) arg0).toArray();
        }
    }

    @Override
    public boolean hasChildren(Object arg0) {
        return ((ITreeNode) arg0).hasChildren();
    }

    @Override
    public Object getParent(Object arg0) {
        return ((ITreeNode) arg0).getParent();
    }
}