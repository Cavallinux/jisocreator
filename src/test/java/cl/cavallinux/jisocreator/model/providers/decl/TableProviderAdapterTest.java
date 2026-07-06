package cl.cavallinux.jisocreator.model.providers.decl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TableProviderAdapter tests")
class TableProviderAdapterTest {
    private final TableProviderAdapter adapter = new TableProviderAdapter();

    @Test
    @DisplayName("Should expose null defaults for content and labels")
    void shouldExposeNullDefaultsForContentAndLabels() {
        assertNull(adapter.getElements(new Object()));
        assertNull(adapter.getColumnImage(new Object(), 0));
        assertNull(adapter.getColumnText(new Object(), 0));
        assertFalse(adapter.isLabelProperty(new Object(), "any"));
    }

    @Test
    @DisplayName("Should accept no-op lifecycle calls")
    void shouldAcceptNoOpLifecycleCalls() {
        adapter.addListener(null);
        adapter.removeListener(null);
        adapter.inputChanged(null, null, null);
        adapter.dispose();
    }
}
