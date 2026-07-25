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
 * Tests para {@link ExitApplicationAction}.
 *
 * <p>{@code run()} y sus metodos privados dependen de {@code GUIManager.INSTANCE}
 * y de {@code IOManager.INSTANCE}, y {@code halt()} invoca {@code System.exit(0)}
 * directamente, por lo que no son invocables de forma segura en un test unitario
 * headless (matarian la JVM del test runner). Se cubre unicamente el estado
 * construido via builder, igual que en {@code AboutActionTest}.
 */
@DisplayName("ExitApplicationAction tests")
class ExitApplicationActionTest {

    @Test
    @DisplayName("Builder should set message, tooltip and imageDescriptor")
    void builderShouldSetMessageTooltipAndImageDescriptor() {
        ImageDescriptor descriptor = ImageDescriptor.getMissingImageDescriptor();

        ExitApplicationAction action = ExitApplicationAction.builder().message("Exit").tooltip("Exit tooltip")
                .imageDescriptor(descriptor).build();

        assertEquals("Exit", action.getText());
        assertEquals("Exit tooltip", action.getToolTipText());
        assertSame(descriptor, action.getImageDescriptor());
    }

    @Test
    @DisplayName("Should be an instance of JISOCreatorBaseAction")
    void shouldBeInstanceOfJISOCreatorBaseAction() {
        ExitApplicationAction action = ExitApplicationAction.builder().build();

        assertTrue(action instanceof JISOCreatorBaseAction);
    }

    @Test
    @DisplayName("Each builder call should produce an independent instance")
    void eachBuilderCallShouldProduceIndependentInstance() {
        ExitApplicationAction action = ExitApplicationAction.builder().message("First").build();
        ExitApplicationAction anotherAction = ExitApplicationAction.builder().message("Second").build();

        assertNotSame(action, anotherAction);
        assertEquals("First", action.getText());
        assertEquals("Second", anotherAction.getText());
    }
}
