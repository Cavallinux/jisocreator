package cl.cavallinux.jisocreator.model.providers.decl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TreeContentAdapter tests")
class TreeContentAdapterTest {
    private final TreeContentAdapter adapter = new TreeContentAdapter();

    @Test
    @DisplayName("Should expose empty/null defaults")
    void shouldExposeEmptyNullDefaults() {
        assertArrayEquals(new Object[0], adapter.getChildren(new Object()));
        assertArrayEquals(new Object[0], adapter.getElements(new Object()));
        assertNull(adapter.getParent(new Object()));
        assertFalse(adapter.hasChildren(new Object()));
    }

    @Test
    @DisplayName("Should accept no-op lifecycle calls")
    void shouldAcceptNoOpLifecycleCalls() {
        adapter.inputChanged(null, null, null);
        adapter.dispose();
    }
}
