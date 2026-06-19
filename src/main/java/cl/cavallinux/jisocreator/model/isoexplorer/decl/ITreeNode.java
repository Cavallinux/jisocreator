package cl.cavallinux.jisocreator.model.isoexplorer.decl;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.graphics.Image;

/**
 * Representacion base de un nodo dentro del sistema de archivos ISO
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version <version>
 * @since <version>
 */
public interface ITreeNode {
    /**
     * Retorna el objeto que contiene el nodo
     * 
     * @return un {@link Object}
     */
    default Object getElement() {
        return null;
    }

    /**
     * Obtiene el nombre reducido del archivo o directorio
     * 
     * @return un {@link String}
     */
    default String getShortName() {
        return StringUtils.EMPTY;
    }

    /**
     * Obtiene el nombre completo del archivo o directorio
     * 
     * @return un {@link String}
     */
    default String getExtendedName() {
        return StringUtils.EMPTY;
    }

    /**
     * Obtiene el nombre ISO del nodo
     * 
     * @return un {@link String}
     */
    default String getIsoName() {
        return StringUtils.EMPTY;
    }

    /**
     * Obtiene el nodo padre.
     * 
     * @return un {@link ITreeNode}
     */
    default ITreeNode getParent() {
        return null;
    }

    /**
     * Obtiene los nodos hijos
     * 
     * @return un arreglo con los nodos hijos
     */
    default Object[] getChildren() {
        return null;
    }

    /**
     * Obtiene la imagen asociada al nodo
     * 
     * @return una {@link Image}
     */
    default Image getImage() {
        return null;
    }

    /**
     * Verifica si el nodo tiene hijos
     * 
     * @return <code>true</code> si el nodo tiene hijos o <code>false</code> en caso
     *         contrario.
     */
    default boolean hasChildren() {
        return false;
    }

    /**
     * Verifica si el nodo es la raiz del sistema de archivos ISO
     * 
     * @return <code>true</code> si el nodo es raiz o <code>false</code> en caso
     *         contrario,
     */
    default boolean isRoot() {
        return false;
    }

    /**
     * Agrega el nodo como hijo al sistema de archivos ISO
     * 
     * @param node Nodo a ser agregado.
     */
    default void addNode(ITreeNode node) {
    }

    /**
     * Borra el nodo asociado.
     * 
     * @param node nodo a ser borrado
     */
    default void deleteNode(ITreeNode node) {
    };
}
