package cl.cavallinux.jisocreator.model.isoexplorer.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;

public class IsoFileSystem {
    private List<String> isoPaths;
    private ITreeNode root;
    private String volumeID;
    private String applicationID;
    private long isoLength;

    public IsoFileSystem(String volumeID, String applicationID) {
	root = new IsoTreeNode();
	this.isoPaths = null;
	this.volumeID = volumeID;
	this.applicationID = applicationID;
	setIsoLength();
    }

    public IsoFileSystem() {
	this("volumeID", "applicationID");
    }

    public ITreeNode[] toArray() {
	return new ITreeNode[] { root };
    }

    public ITreeNode getRoot() {
	return root;
    }

    public String getVolumeID() {
	return volumeID;
    }

    public void setVolumeID(String volumeID) {
	this.volumeID = volumeID;
    }

    public String getApplicationID() {
	return applicationID;
    }

    public void setApplicationID(String applicationID) {
	this.applicationID = applicationID;
    }

    public long getIsoLength() {
	return isoLength;
    }

    public void setIsoLength(long isoLength) {
	this.isoLength = isoLength;
    }

    public void setIsoLength() {
	isoLength = calculateIsoSize(root);
    }

    public List<String> getPaths() {
	return isoPaths;
    }

    public void setIsoPaths(List<String> isoPaths) {
	this.isoPaths = isoPaths;
    }

    /**
     * Parsing since root
     */
    public void parse() {
	isoPaths = new ArrayList<String>();
	for (Object child : root.getChildren()) {
	    parse((ITreeNode) child);
	}
    }

    /**
     * Parsing since any node
     */
    private void parse(ITreeNode node) {
	if (node.hasChildren()) {
	    for (Object child : node.getChildren()) {
		parse((ITreeNode) child);
	    }
	} else {
	    String isoPath = node.getIsoName().concat("=").concat(node.getExtendedName());
	    isoPaths.add(isoPath);
	}
    }

    private long calculateIsoSize(ITreeNode node) {
	long size = 0;
	if (node.isRoot() || node.hasChildren()) {
	    for (Object child : node.getChildren()) {
		size += calculateIsoSize((ITreeNode) child);
	    }
	} else {
	    size += ((File) node.getElement()).length();
	}
	return size;
    }
}