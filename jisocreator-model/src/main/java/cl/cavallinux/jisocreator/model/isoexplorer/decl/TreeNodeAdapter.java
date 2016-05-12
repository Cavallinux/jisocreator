package cl.cavallinux.jisocreator.model.isoexplorer.decl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;

public abstract class TreeNodeAdapter implements ITreeNode {

    @Override
    public void addNode(ITreeNode node, IProgressMonitor monitor) {
	// TODO Auto-generated method stub

    }

    @Override
    public void deleteNode(ITreeNode node, IProgressMonitor monitor) {
	// TODO Auto-generated method stub

    }

    @Override
    public Object[] getChildren() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getExtendedName() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Image getImage() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ITreeNode getParent() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getShortName() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public boolean hasChildren() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public Object getElement() {
	return null;
    }

    @Override
    public boolean isRoot() {
	return false;
    }

    @Override
    public String getIsoName() {
	return null;
    }

}
