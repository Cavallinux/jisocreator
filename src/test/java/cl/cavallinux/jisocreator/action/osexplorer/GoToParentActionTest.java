package cl.cavallinux.jisocreator.action.osexplorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jface.resource.ImageDescriptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;

/**
 * Tests para {@link GoToParentAction}.
 *
 * <p>{@code run()} depende de {@code GUIManager.INSTANCE.getMainWindow()}
 * (arbol real del explorador de SO) y de
 * {@code OSAndIsoExplorerManager.INSTANCE.getOsExplorer()}, por lo que no es
 * invocable de forma headless. Se cubre la construccion via builder
 * (incluyendo el estado inicial {@code setEnabled(false)}) y los chequeos de
 * tipo, siguiendo el mismo patron que el resto de acciones ya cubiertas
 * (Grupos 3 y 4).
 */
@DisplayName("GoToParentAction tests")
class GoToParentActionTest {

    @Test
    @DisplayName("Builder should set message, tooltip and imageDescriptor")
    void builderShouldSetMessageTooltipAndImageDescriptor() {
        ImageDescriptor descriptor = ImageDescriptor.getMissingImageDescriptor();

        GoToParentAction action = GoToParentAction.builder().message("Go to parent").tooltip("Go to parent tooltip")
                .imageDescriptor(descriptor).build();

        assertEquals("Go to parent", action.getText());
        assertEquals("Go to parent tooltip", action.getToolTipText());
        assertSame(descriptor, action.getImageDescriptor());
    }

    @Test
    @DisplayName("Builder should initialise the action as disabled")
    void builderShouldInitialiseActionAsDisabled() {
        GoToParentAction action = GoToParentAction.builder().build();

        assertFalse(action.isEnabled());
    }

    @Test
    @DisplayName("Should be an instance of JISOCreatorBaseAction")
    void shouldBeInstanceOfJISOCreatorBaseAction() {
        GoToParentAction action = GoToParentAction.builder().build();

        assertTrue(action instanceof JISOCreatorBaseAction);
    }

    @Test
    @DisplayName("Each builder call should produce an independent instance")
    void eachBuilderCallShouldProduceIndependentInstance() {
        GoToParentAction action = GoToParentAction.builder().message("First").build();
        GoToParentAction anotherAction = GoToParentAction.builder().message("Second").build();

        assertNotSame(action, anotherAction);
        assertEquals("First", action.getText());
        assertEquals("Second", anotherAction.getText());
    }
}
