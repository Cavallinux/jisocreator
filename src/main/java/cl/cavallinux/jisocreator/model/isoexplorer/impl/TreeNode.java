package cl.cavallinux.jisocreator.model.isoexplorer.impl;

import java.util.ArrayList;
import java.util.List;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;

/**
 * Implementacion de la interface {@link ITreeNode}, implementacion real de un nodo
 * de un arbol con multiples hijos.
 * 
 * @author Paolo Mezzano Barahona
 * @version 0.1.5
 * @version 0.0.2
 */
public abstract class TreeNode implements ITreeNode {
    protected ITreeNode parent;
    protected List<ITreeNode> children;

    protected TreeNode(ITreeNode parent) {
        this.parent = parent;
        children = new ArrayList<ITreeNode>();
    }
    
    protected TreeNode() {
        this(null);
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