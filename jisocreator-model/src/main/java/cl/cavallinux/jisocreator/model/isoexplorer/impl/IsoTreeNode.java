package cl.cavallinux.jisocreator.model.isoexplorer.impl;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.util.ImageUtils;

public class IsoTreeNode extends TreeNode {
    private File file;
    private String isoName;
    private boolean isRoot;

    public IsoTreeNode(ITreeNode parent, File file, boolean isRoot) {
	super(parent);
	this.file = file;
	this.isRoot = isRoot;
	if (isRoot) {
	    this.isoName = "/";
	} else if (file.isDirectory()) {
	    this.isoName = parent.getIsoName().concat(getShortName()).concat("/");
	} else {
	    this.isoName = parent.getIsoName().concat(getShortName());
	}
    }

    public IsoTreeNode(ITreeNode parent, File file) {
	this(parent, file, false);
    }

    public IsoTreeNode() {
	this(null, null, true);
    }

    @Override
    public String getIsoName() {
	return isoName;
    }

    @Override
    public Object getElement() {
	return file;
    }

    @Override
    public String getShortName() {
	return file.getName();
    }

    @Override
    public String getExtendedName() {
	return file.getAbsolutePath();
    }

    @Override
    public void addNode(ITreeNode node, IProgressMonitor monitor) {
	if (children.contains(node)) {
	    return;
	} else {
	    monitor.subTask("Adding file: ".concat(node.getExtendedName()));
	    children.add(node);

	    File[] childs = ((File) node.getElement()).listFiles();
	    monitor.worked(1);
	    if (childs == null) {
		return;
	    } else {
		for (File child : childs) {
		    ITreeNode newNode = new IsoTreeNode(node, child);
		    node.addNode(newNode, monitor);
		}
	    }
	}
    }

    @Override
    public void deleteNode(ITreeNode node, IProgressMonitor monitor) {
	if (children.contains(node)) {
	    children.remove(node);
	} else {
	    return;
	}
    }

    @Override
    public Image getImage() {
	return ImageUtils.getInstance().loadImage(this);
    }

    @Override
    public boolean isRoot() {
	return isRoot;
    }
}