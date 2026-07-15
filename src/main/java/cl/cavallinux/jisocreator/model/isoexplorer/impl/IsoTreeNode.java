package cl.cavallinux.jisocreator.model.isoexplorer.impl;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import org.eclipse.swt.graphics.Image;

import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import lombok.extern.slf4j.Slf4j;

/**
 * Extension de la clase {@link TreeNode}, implementacion de un nodo del arbol
 * de directorios ISO
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.1.5
 * @since 0.0.2
 */
@Slf4j
public class IsoTreeNode extends TreeNode {
    private File file;
    private String isoName;
    private boolean isRoot;
    private static final String ROOT_ISO_NAME = "/";

    public IsoTreeNode(ITreeNode parent, File file, boolean isRoot) {
        super(parent);
        this.file = file;
        this.isRoot = isRoot;
        this.isoName = isRoot ? ROOT_ISO_NAME : setIsoName();
    }

    public IsoTreeNode(ITreeNode parent, File file, String isoName, boolean isRoot) {
        super(parent);
        this.file = file;
        this.isRoot = isRoot;
        this.isoName = isRoot ? ROOT_ISO_NAME : (isoName == null ? setIsoName() : isoName);
    }

    private String setIsoName() {
        String parentIsoShortName = this.parent.getIsoName().concat(getShortName());
        return this.file.isDirectory() ? parentIsoShortName.concat("/") : parentIsoShortName;
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
    public void addNode(ITreeNode node) {
        if (!children.contains(node)) {
            children.add(node);
            log.debug("Added node: {} to parent: {}", node.getIsoName(), this.getIsoName());
            File[] childs = node.getElement() instanceof File file ? file.listFiles() : null;
            Optional.ofNullable(childs).ifPresent(files -> Arrays.stream(files).forEach(child -> {
                ITreeNode newNode = new IsoTreeNode(node, child);
                node.addNode(newNode);
            }));
        }
    }

    @Override
    public void addLeafNode(ITreeNode node) {
        if (!children.contains(node)) {
            children.add(node);
            log.debug("Added leaf node: {} to parent: {}", node.getIsoName(), this.getIsoName());
        }
    }

    @Override
    public void deleteNode(ITreeNode node) {
        if (children.contains(node)) {
            children.remove(node);
        } else {
            return;
        }
    }

    @Override
    public Image getImage() {
        return ImageRegister.INSTANCE.getImageUtils().loadImage(this);
    }

    @Override
    public boolean isRoot() {
        return isRoot;
    }
}