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
 * Tests para {@link AboutAction}.
 *
 * <p>El metodo {@link AboutAction#run()} despliega {@code AboutDialog} usando
 * {@code GUIManager.INSTANCE.getMainWindow()}, por lo que no puede ejecutarse
 * de forma headless sin una ventana principal real. Estos tests cubren el
 * estado construido via builder, siguiendo el mismo enfoque usado en
 * {@code SaveAsIsoActionTest} para las clases de acciones acopladas a la GUI.
 */
@DisplayName("AboutAction tests")
class AboutActionTest {

    @Test
    @DisplayName("Builder should set message, tooltip and imageDescriptor")
    void builderShouldSetMessageTooltipAndImageDescriptor() {
        ImageDescriptor descriptor = ImageDescriptor.getMissingImageDescriptor();

        AboutAction action = AboutAction.builder().message("About").tooltip("About tooltip")
                .imageDescriptor(descriptor).build();

        assertEquals("About", action.getText());
        assertEquals("About tooltip", action.getToolTipText());
        assertSame(descriptor, action.getImageDescriptor());
    }

    @Test
    @DisplayName("Should be an instance of JISOCreatorBaseAction")
    void shouldBeInstanceOfJISOCreatorBaseAction() {
        AboutAction action = AboutAction.builder().build();

        assertTrue(action instanceof JISOCreatorBaseAction);
    }

    @Test
    @DisplayName("Each builder call should produce an independent instance")
    void eachBuilderCallShouldProduceIndependentInstance() {
        AboutAction action = AboutAction.builder().message("First").build();
        AboutAction anotherAction = AboutAction.builder().message("Second").build();

        assertNotSame(action, anotherAction);
        assertEquals("First", action.getText());
        assertEquals("Second", anotherAction.getText());
    }
}
