package cl.cavallinux.jisocreator.model.providers.isoexplorer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;

@DisplayName("IsoTreeContentProvider tests")
class IsoTreeContentProviderTest {
    private final IsoTreeContentProvider provider = new IsoTreeContentProvider();

    @Test
    @DisplayName("Should return root from IsoFileSystem input")
    void shouldReturnRootFromIsoFilesystemInput() {
        StubTreeNode root = new StubTreeNode("/", true);
        IsoFileSystem iso = new IsoFileSystem(null, root, "VOL", "APP", "PUB", 0L);

        Object[] elements = provider.getElements(iso);
        Object[] children = provider.getChildren(iso);

        assertArrayEquals(new Object[] { root }, elements);
        assertArrayEquals(new Object[] { root }, children);
    }

    @Test
    @DisplayName("Should return node children and parent relationships")
    void shouldReturnNodeChildrenAndParentRelationships() {
        StubTreeNode root = new StubTreeNode("/", true);
        StubTreeNode child = new StubTreeNode("/docs", false);
        root.addChild(child);

        Object[] children = provider.getChildren(root);

        assertArrayEquals(new Object[] { child }, children);
        assertSame(root, provider.getParent(child));
        assertTrue(provider.hasChildren(root));
        assertFalse(provider.hasChildren(child));
    }

    private static final class StubTreeNode implements ITreeNode {
        private final String isoName;
        private final boolean root;
        private final List<ITreeNode> children = new ArrayList<>();
        private ITreeNode parent;

        private StubTreeNode(String isoName, boolean root) {
            this.isoName = isoName;
            this.root = root;
        }

        private void addChild(StubTreeNode child) {
            child.parent = this;
            children.add(child);
        }

        @Override
        public String getIsoName() {
            return isoName;
        }

        @Override
        public ITreeNode getParent() {
            return parent;
        }

        @Override
        public List<ITreeNode> getChildren() {
            return children;
        }

        @Override
        public Object[] toArray() {
            return children.toArray();
        }

        @Override
        public boolean hasChildren() {
            return !children.isEmpty();
        }

        @Override
        public boolean isRoot() {
            return root;
        }
    }
}
