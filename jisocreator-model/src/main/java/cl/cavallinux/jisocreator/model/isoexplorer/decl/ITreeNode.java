package cl.cavallinux.jisocreator.model.isoexplorer.decl;

import org.eclipse.core.runtime.IProgressMonitor;
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
    Object getElement();

    /**
     * Obtiene el nombre reducido del archivo o directorio
     * 
     * @return un {@link String}
     */
    String getShortName();

    /**
     * Obtiene el nombre completo del archivo o directorio
     * 
     * @return un {@link String}
     */
    String getExtendedName();

    /**
     * Obtiene el nombre ISO del nodo
     * 
     * @return un {@link String}
     */
    String getIsoName();

    /**
     * Obtiene el nodo padre.
     * 
     * @return un {@link ITreeNode}
     */
    ITreeNode getParent();

    /**
     * Obtiene los nodos hijos
     * 
     * @return un arreglo con los nodos hijos
     */
    Object[] getChildren();

    /**
     * Obtiene la imagen asociada al nodo
     * 
     * @return una {@link Image}
     */
    Image getImage();

    /**
     * Verifica si el nodo tiene hijos
     * 
     * @return <code>true</code> si el nodo tiene hijos o <code>false</code> en caso
     *         contrario.
     */
    boolean hasChildren();

    /**
     * Verifica si el nodo es la raiz del sistema de archivos ISO
     * 
     * @return <code>true</code> si el nodo es raiz o <code>false</code> en caso
     *         contrario,
     */
    boolean isRoot();

    /**
     * Agrega el nodo como hijo al sistema de archivos ISO
     * 
     * @param node    Nodo a ser agregado.
     * @param monitor monitor de progreso asociado.
     */
    void addNode(ITreeNode node, IProgressMonitor monitor);

    /**
     * Borra el nodo asociado.
     * 
     * @param node    nodo a ser borrado
     * @param monitor monitor de progreso asociado.
     */
    void deleteNode(ITreeNode node, IProgressMonitor monitor);
}
