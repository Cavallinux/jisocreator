package cl.cavallinux.jisocreator.model.isoexplorer.impl;

import java.util.ArrayList;
import java.util.List;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.TreeNodeAdapter;

/**
 * Extension de la clase {@link TreeNodeAdapter}, implementacion real de un nodo
 * de un arbol con multiples hijos.
 * 
 * @author Paolo Mezzano Barahona
 * @version 0.0.2
 * @version 0.0.2
 */
public class TreeNode extends TreeNodeAdapter {
    protected ITreeNode parent;
    protected List<ITreeNode> children;

    public TreeNode(ITreeNode parent) {
        this.parent = parent;
        children = new ArrayList<ITreeNode>();
    }

    @Override
    public Object[] getChildren() {
        return children.toArray();
    }

    @Override
    public ITreeNode getParent() {
        return parent;
    }

    @Override
    public boolean hasChildren() {
        return !children.isEmpty();
    }
}