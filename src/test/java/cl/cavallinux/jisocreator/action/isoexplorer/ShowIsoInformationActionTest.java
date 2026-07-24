package cl.cavallinux.jisocreator.action.isoexplorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jface.resource.ImageDescriptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;

/**
 * Tests para {@link ShowIsoInformationAction}.
 *
 * <p>{@code run()} depende de {@code GUIManager.INSTANCE.getMainWindow()} y
 * abre un dialogo modal real ({@code ShowIsoLayoutInformationDialog}), por lo
 * que no es invocable de forma headless. Se cubre la construccion via
 * builder (incluyendo el estado inicial {@code setEnabled(true)}, distinto
 * al resto de acciones del grupo) y los chequeos de tipo.
 */
@DisplayName("ShowIsoInformationAction tests")
class ShowIsoInformationActionTest {

    @Test
    @DisplayName("Builder should set message, tooltip and imageDescriptor")
    void builderShouldSetMessageTooltipAndImageDescriptor() {
        ImageDescriptor descriptor = ImageDescriptor.getMissingImageDescriptor();

        ShowIsoInformationAction action = ShowIsoInformationAction.builder().message("Show information")
                .tooltip("Show information tooltip").imageDescriptor(descriptor).build();

        assertEquals("Show information", action.getText());
        assertEquals("Show information tooltip", action.getToolTipText());
        assertSame(descriptor, action.getImageDescriptor());
    }

    @Test
    @DisplayName("Builder should initialise the action as enabled")
    void builderShouldInitialiseActionAsEnabled() {
        ShowIsoInformationAction action = ShowIsoInformationAction.builder().build();

        assertTrue(action.isEnabled());
    }

    @Test
    @DisplayName("Should be an instance of JISOCreatorBaseAction")
    void shouldBeInstanceOfJISOCreatorBaseAction() {
        ShowIsoInformationAction action = ShowIsoInformationAction.builder().build();

        assertTrue(action instanceof JISOCreatorBaseAction);
    }

    @Test
    @DisplayName("Each builder call should produce an independent instance")
    void eachBuilderCallShouldProduceIndependentInstance() {
        ShowIsoInformationAction action = ShowIsoInformationAction.builder().message("First").build();
        ShowIsoInformationAction anotherAction = ShowIsoInformationAction.builder().message("Second").build();

        assertNotSame(action, anotherAction);
        assertEquals("First", action.getText());
        assertEquals("Second", anotherAction.getText());
    }
}
