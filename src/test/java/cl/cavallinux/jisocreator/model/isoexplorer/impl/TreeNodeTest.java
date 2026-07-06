package cl.cavallinux.jisocreator.model.isoexplorer.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;

@DisplayName("TreeNode tests")
class TreeNodeTest {
    @Test
    @DisplayName("Should keep parent and children references")
    void shouldKeepParentAndChildrenReferences() {
        SimpleTreeNode parent = new SimpleTreeNode(null);
        SimpleTreeNode node = new SimpleTreeNode(parent);
        SimpleTreeNode childA = new SimpleTreeNode(node);
        SimpleTreeNode childB = new SimpleTreeNode(node);
        node.setChildren(List.of(childA, childB));

        assertSame(parent, node.getParent());
        assertEquals(2, node.getChildren().size());
        assertSame(childA, node.getChildren().get(0));
        assertSame(childB, node.getChildren().get(1));
    }

    @Test
    @DisplayName("Should report hasChildren based on children list")
    void shouldReportHasChildrenBasedOnChildrenList() {
        SimpleTreeNode node = new SimpleTreeNode(null);
        assertFalse(node.hasChildren());

        node.setChildren(List.of(new SimpleTreeNode(node)));
        assertTrue(node.hasChildren());
    }

    @Test
    @DisplayName("Should expose children through toArray in same order")
    void shouldExposeChildrenThroughToArrayInSameOrder() {
        SimpleTreeNode node = new SimpleTreeNode(null);
        SimpleTreeNode childA = new SimpleTreeNode(node);
        SimpleTreeNode childB = new SimpleTreeNode(node);
        node.setChildren(List.of(childA, childB));

        assertArrayEquals(new Object[] { childA, childB }, node.toArray());
    }

    private static final class SimpleTreeNode extends TreeNode {
        private SimpleTreeNode(ITreeNode parent) {
            super(parent);
        }
    }
}
