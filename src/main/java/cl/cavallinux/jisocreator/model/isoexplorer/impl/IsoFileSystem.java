package cl.cavallinux.jisocreator.model.isoexplorer.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.Strings;

import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Sistema de archivos ISO, que parte con el nodo raiz y algunos atributos
 * propios de la imagen ISO
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.1.5
 * @since 0.0.1
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IsoFileSystem {
    private static String ISOFILESYSTEM_INFO = "ISO 9660 Layout -> Volume ID: <volume_id>, Length: <length> bytes";
    @Builder.Default
    private List<String> isoPaths = null;
    @Builder.Default
    private ITreeNode root = new IsoTreeNode();
    @Builder.Default
    private String volumeID = IOManager.INSTANCE.getIsoFilesystemParser().generateInitialVolumeID();
    @Builder.Default
    private String applicationID = IOManager.INSTANCE.getIsoFilesystemParser().generateIsoFilesystemApplicationID();
    @Builder.Default
    private String publisherID = UUID.randomUUID().toString();
    @Builder.Default
    private Long isoLength = 0l;

    public ITreeNode[] toArray() {
        return List.of(root).toArray(ITreeNode[]::new);
    }

    public void setIsoLength() {
        isoLength = calculateIsoSize(root);
    }
    
    public String printIsoFileSystemInfo(String isoLayoutInfoTemplate) {
        isoLayoutInfoTemplate = Strings.CI.replace(isoLayoutInfoTemplate, "<volume_id>", volumeID);
        isoLayoutInfoTemplate = Strings.CI.replace(isoLayoutInfoTemplate, "<length>", String.valueOf(isoLength));
        StringBuffer buffer = new StringBuffer();
        buffer.append(isoLayoutInfoTemplate);
        return buffer.toString();
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