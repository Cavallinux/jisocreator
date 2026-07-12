package cl.cavallinux.jisocreator.model.providers.isoexplorer;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;

public class IsoTreeLabelProvider extends LabelProvider {
    @Override
    public Image getImage(Object element) {
        return ((ITreeNode) element).getImage();
    }

    @Override
    public String getText(Object element) {
        ITreeNode isoTreeNodeElement = (ITreeNode) element;
        return isoTreeNodeElement.isRoot() ? isoTreeNodeElement.getIsoName() : isoTreeNodeElement.getShortName();
    }
}