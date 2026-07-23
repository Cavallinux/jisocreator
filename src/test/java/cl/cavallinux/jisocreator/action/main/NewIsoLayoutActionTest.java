package cl.cavallinux.jisocreator.action.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jface.resource.ImageDescriptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;

/**
 * Tests para {@link NewIsoLayoutAction}.
 *
 * <p>{@code run()} construye un {@code IsoFileSystem} nuevo pero luego lo
 * inserta en el arbol de {@code GUIManager.INSTANCE.getMainWindow().getIsoExplorer()},
 * por lo que no puede ejecutarse de forma headless. Se cubre la construccion
 * via builder.
 */
@DisplayName("NewIsoLayoutAction tests")
class NewIsoLayoutActionTest {

    @Test
    @DisplayName("Builder should set message, tooltip and imageDescriptor")
    void builderShouldSetMessageTooltipAndImageDescriptor() {
        ImageDescriptor descriptor = ImageDescriptor.getMissingImageDescriptor();

        NewIsoLayoutAction action = NewIsoLayoutAction.builder().message("New").tooltip("New tooltip")
                .imageDescriptor(descriptor).build();

        assertEquals("New", action.getText());
        assertEquals("New tooltip", action.getToolTipText());
        assertSame(descriptor, action.getImageDescriptor());
    }

    @Test
    @DisplayName("Should be an instance of JISOCreatorBaseAction")
    void shouldBeInstanceOfJISOCreatorBaseAction() {
        NewIsoLayoutAction action = NewIsoLayoutAction.builder().build();

        assertTrue(action instanceof JISOCreatorBaseAction);
    }

    @Test
    @DisplayName("Each builder call should produce an independent instance")
    void eachBuilderCallShouldProduceIndependentInstance() {
        NewIsoLayoutAction action = NewIsoLayoutAction.builder().message("First").build();
        NewIsoLayoutAction anotherAction = NewIsoLayoutAction.builder().message("Second").build();

        assertNotSame(action, anotherAction);
        assertEquals("First", action.getText());
        assertEquals("Second", anotherAction.getText());
    }
}
