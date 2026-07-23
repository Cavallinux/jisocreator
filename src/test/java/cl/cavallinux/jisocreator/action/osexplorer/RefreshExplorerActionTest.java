package cl.cavallinux.jisocreator.action.osexplorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jface.resource.ImageDescriptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;

/**
 * Tests para {@link RefreshExplorerAction}.
 *
 * <p>{@code run()} depende de {@code GUIManager.INSTANCE.getMainWindow()},
 * por lo que no es invocable de forma headless. A diferencia de
 * {@code GoToParentAction}/{@code OpenAction}, el constructor de esta accion
 * no llama a {@code setEnabled(false)}, por lo que queda habilitada por
 * defecto (comportamiento heredado de {@code org.eclipse.jface.action.Action}).
 * Se cubre la construccion via builder, ese estado inicial y los chequeos de
 * tipo.
 */
@DisplayName("RefreshExplorerAction tests")
class RefreshExplorerActionTest {

    @Test
    @DisplayName("Builder should set message, tooltip and imageDescriptor")
    void builderShouldSetMessageTooltipAndImageDescriptor() {
        ImageDescriptor descriptor = ImageDescriptor.getMissingImageDescriptor();

        RefreshExplorerAction action = RefreshExplorerAction.builder().message("Refresh").tooltip("Refresh tooltip")
                .imageDescriptor(descriptor).build();

        assertEquals("Refresh", action.getText());
        assertEquals("Refresh tooltip", action.getToolTipText());
        assertSame(descriptor, action.getImageDescriptor());
    }

    @Test
    @DisplayName("Builder should leave the action enabled by default (no setEnabled(false) call)")
    void builderShouldLeaveActionEnabledByDefault() {
        RefreshExplorerAction action = RefreshExplorerAction.builder().build();

        assertTrue(action.isEnabled());
    }

    @Test
    @DisplayName("Should be an instance of JISOCreatorBaseAction")
    void shouldBeInstanceOfJISOCreatorBaseAction() {
        RefreshExplorerAction action = RefreshExplorerAction.builder().build();

        assertTrue(action instanceof JISOCreatorBaseAction);
    }

    @Test
    @DisplayName("Each builder call should produce an independent instance")
    void eachBuilderCallShouldProduceIndependentInstance() {
        RefreshExplorerAction action = RefreshExplorerAction.builder().message("First").build();
        RefreshExplorerAction anotherAction = RefreshExplorerAction.builder().message("Second").build();

        assertNotSame(action, anotherAction);
        assertEquals("First", action.getText());
        assertEquals("Second", anotherAction.getText());
    }
}
