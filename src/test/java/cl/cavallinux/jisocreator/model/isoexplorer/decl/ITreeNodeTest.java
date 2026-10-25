package cl.cavallinux.jisocreator.model.isoexplorer.decl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests para los métodos default de {@link ITreeNode}.
 *
 * <p>Los métodos default de esta interfaz son ejercitados indirectamente por
 * {@code IsoTreeNodeTest} y {@code TreeNodeTest} a través de sus
 * implementaciones concretas, pero ninguna de ellas cubre explícitamente los
 * valores por defecto (sin overrides) declarados aquí. Este test usa un doble
 * mínimo que no sobreescribe ningún método default, análogo al patrón usado
 * en {@code ICommandLineParserTest} para {@code ICommandLineParser}.</p>
 */
@DisplayName("ITreeNode default method tests")
class ITreeNodeTest {

    /** Implementación mínima que no sobreescribe ningún método default. */
    private static class BareTreeNode implements ITreeNode {
    }

    private ITreeNode node;

    @BeforeEach
    void setUp() {
        node = new BareTreeNode();
    }

    @Test
    @DisplayName("getElement should default to null")
    void getElementShouldDefaultToNull() {
        assertNull(node.getElement());
    }

    @Test
    @DisplayName("getShortName should default to empty string")
    void getShortNameShouldDefaultToEmptyString() {
        assertEquals("", node.getShortName());
    }

    @Test
    @DisplayName("getExtendedName should default to empty string")
    void getExtendedNameShouldDefaultToEmptyString() {
        assertEquals("", node.getExtendedName());
    }

    @Test
    @DisplayName("getIsoName should default to empty string")
    void getIsoNameShouldDefaultToEmptyString() {
        assertEquals("", node.getIsoName());
    }

    @Test
    @DisplayName("getParent should default to null")
    void getParentShouldDefaultToNull() {
        assertNull(node.getParent());
    }

    @Test
    @DisplayName("getChildren should default to an empty list")
    void getChildrenShouldDefaultToEmptyList() {
        assertTrue(node.getChildren().isEmpty());
    }

    @Test
    @DisplayName("toArray should default to null")
    void toArrayShouldDefaultToNull() {
        assertNull(node.toArray());
    }

    @Test
    @DisplayName("getImage should default to null")
    void getImageShouldDefaultToNull() {
        assertNull(node.getImage());
    }

    @Test
    @DisplayName("hasChildren should default to false")
    void hasChildrenShouldDefaultToFalse() {
        assertFalse(node.hasChildren());
    }

    @Test
    @DisplayName("isRoot should default to false")
    void isRootShouldDefaultToFalse() {
        assertFalse(node.isRoot());
    }

    @Test
    @DisplayName("addNode should be a no-op by default")
    void addNodeShouldBeNoOpByDefault() {
        node.addNode(new BareTreeNode());
        /** No exception and no observable state change: children remain empty. */
        assertTrue(node.getChildren().isEmpty());
    }

    @Test
    @DisplayName("addLeafNode should be a no-op by default")
    void addLeafNodeShouldBeNoOpByDefault() {
        node.addLeafNode(new BareTreeNode());
        assertTrue(node.getChildren().isEmpty());
    }

    @Test
    @DisplayName("deleteNode should be a no-op by default")
    void deleteNodeShouldBeNoOpByDefault() {
        node.deleteNode(new BareTreeNode());
        assertTrue(node.getChildren().isEmpty());
    }
}
