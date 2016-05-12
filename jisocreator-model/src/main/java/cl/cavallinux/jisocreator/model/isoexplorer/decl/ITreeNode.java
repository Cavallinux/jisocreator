package cl.cavallinux.jisocreator.model.isoexplorer.decl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;

public interface ITreeNode {
    public Object getElement();

    public String getShortName();

    public String getExtendedName();

    public String getIsoName();

    public ITreeNode getParent();

    public Object[] getChildren();

    public Image getImage();

    public boolean hasChildren();

    public boolean isRoot();

    public void addNode(ITreeNode node, IProgressMonitor monitor);

    public void deleteNode(ITreeNode node, IProgressMonitor monitor);
}
