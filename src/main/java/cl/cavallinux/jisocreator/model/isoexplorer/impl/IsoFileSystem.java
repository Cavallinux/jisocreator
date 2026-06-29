package cl.cavallinux.jisocreator.model.isoexplorer.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.Strings;

import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class IsoFileSystem {
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
        log.info("Atomic iso layout length: {}", isoLength);
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
        AtomicLong length = new AtomicLong(0);
        if (node.isRoot() || node.hasChildren()) {
            node.getChildren().forEach(children -> {
                length.addAndGet(calculateIsoSize(children));
            });
        } else {
            File file = (File) node.getElement();
            length.addAndGet(file.length());
        }
        return length.get();
    }
}