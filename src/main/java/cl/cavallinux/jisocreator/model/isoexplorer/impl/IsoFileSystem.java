package cl.cavallinux.jisocreator.model.isoexplorer.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;

/**
 * Sistema de archivos ISO, que parte con el nodo raiz y algunos atributos
 * propios de la imagen ISO
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.1.5
 * @since 0.0.1
 */
@JacksonXmlRootElement(localName = "iso9660")
@JsonIgnoreProperties(ignoreUnknown = true)
public class IsoFileSystem {
    private List<String> isoPaths;
    @JacksonXmlProperty(localName = "RootEntry")
    private ITreeNode root;
    @JacksonXmlProperty(isAttribute = true)
    private String volumeID;
    @JacksonXmlProperty(isAttribute = true)
    private String applicationID;
    @JacksonXmlProperty(isAttribute = true)
    private String publisherID;
    @JacksonXmlProperty(isAttribute = true)
    private long isoLength;

    public IsoFileSystem(String volumeID, String applicationID, String publisherID) {
        root = new IsoTreeNode();
        this.isoPaths = null;
        this.volumeID = volumeID;
        this.applicationID = applicationID;
        this.publisherID = publisherID;
        setIsoLength();
    }

    public IsoFileSystem() {
        this(IOManager.INSTANCE.getIoUtils().generateInitialVolumeID(),
                IOManager.INSTANCE.getIoUtils().generateIsoFilesystemApplicationID(), UUID.randomUUID().toString());
    }
    
    public String getPublisherID() {
        return publisherID;
    }

    public void setPublisherID(String publisherID) {
        this.publisherID = publisherID;
    }

    public ITreeNode[] toArray() {
        return List.of(root).toArray(ITreeNode[]::new);
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