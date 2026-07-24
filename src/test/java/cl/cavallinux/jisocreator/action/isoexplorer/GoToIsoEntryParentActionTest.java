package cl.cavallinux.jisocreator.action.isoexplorer;

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
 * Tests para {@link GoToIsoEntryParentAction}.
 *
 * <p>{@code run()} depende de {@code GUIManager.INSTANCE.getMainWindow()}
 * para localizar el arbol de directorios ISO real, por lo que no es
 * invocable de forma headless. Se cubre la construccion via builder
 * (incluyendo el estado inicial {@code setEnabled(false)}) y los chequeos de
 * tipo.
 */
@DisplayName("GoToIsoEntryParentAction tests")
class GoToIsoEntryParentActionTest {

    @Test
    @DisplayName("Builder should set message, tooltip and imageDescriptor")
    void builderShouldSetMessageTooltipAndImageDescriptor() {
        ImageDescriptor descriptor = ImageDescriptor.getMissingImageDescriptor();

        GoToIsoEntryParentAction action = GoToIsoEntryParentAction.builder().message("Go to parent")
                .tooltip("Go to parent tooltip").imageDescriptor(descriptor).build();

        assertEquals("Go to parent", action.getText());
        assertEquals("Go to parent tooltip", action.getToolTipText());
        assertSame(descriptor, action.getImageDescriptor());
    }

    @Test
    @DisplayName("Builder should initialise the action as disabled")
    void builderShouldInitialiseActionAsDisabled() {
        GoToIsoEntryParentAction action = GoToIsoEntryParentAction.builder().build();

        assertFalse(action.isEnabled());
    }

    @Test
    @DisplayName("Should be an instance of JISOCreatorBaseAction")
    void shouldBeInstanceOfJISOCreatorBaseAction() {
        GoToIsoEntryParentAction action = GoToIsoEntryParentAction.builder().build();

        assertTrue(action instanceof JISOCreatorBaseAction);
    }

    @Test
    @DisplayName("Each builder call should produce an independent instance")
    void eachBuilderCallShouldProduceIndependentInstance() {
        GoToIsoEntryParentAction action = GoToIsoEntryParentAction.builder().message("First").build();
        GoToIsoEntryParentAction anotherAction = GoToIsoEntryParentAction.builder().message("Second").build();

        assertNotSame(action, anotherAction);
        assertEquals("First", action.getText());
        assertEquals("Second", anotherAction.getText());
    }
}
