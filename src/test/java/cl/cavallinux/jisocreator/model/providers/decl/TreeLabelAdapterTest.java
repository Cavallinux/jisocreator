package cl.cavallinux.jisocreator.model.providers.decl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TreeLabelAdapter tests")
class TreeLabelAdapterTest {
    private final TreeLabelAdapter adapter = new TreeLabelAdapter();

    @Test
    @DisplayName("Should delegate default text and image behavior")
    void shouldDelegateDefaultTextAndImageBehavior() {
        Object value = "value";
        assertEquals("value", adapter.getText(value));
        assertNull(adapter.getImage(value));
    }

    @Test
    @DisplayName("Should dispose without throwing")
    void shouldDisposeWithoutThrowing() {
        adapter.dispose();
    }
}
