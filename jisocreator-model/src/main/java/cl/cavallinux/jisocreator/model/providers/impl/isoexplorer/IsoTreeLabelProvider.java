package cl.cavallinux.jisocreator.model.providers.impl.isoexplorer;

import org.eclipse.swt.graphics.Image;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.model.providers.decl.TreeLabelAdapter;

public class IsoTreeLabelProvider extends TreeLabelAdapter {
    @Override
    public Image getImage(Object element) {
	return ((ITreeNode) element).getImage();
    }

    @Override
    public String getText(Object element) {
	if (((ITreeNode) element).isRoot()) {
	    return "Root";
	} else {
	    return ((ITreeNode) element).getShortName();
	}
    }
}