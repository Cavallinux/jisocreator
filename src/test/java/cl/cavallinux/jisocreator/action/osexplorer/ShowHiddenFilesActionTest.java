package cl.cavallinux.jisocreator.action.osexplorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;

/**
 * Tests para {@link ShowHiddenFilesAction}.
 *
 * <p>{@code run()} dispara {@code Display.getCurrent().asyncExec(...)} con un
 * {@link cl.cavallinux.jisocreator.action.jobs.ToggleHiddenFilesOSExplorerThread},
 * por lo que no es invocable de forma headless. Se cubre la construccion via
 * builder (incluyendo el estilo {@code Action.AS_CHECK_BOX} usado por
 * {@code OSExplorerActionsManager.SHOWHIDDENFILES}), el estado inicial
 * habilitado por defecto y los chequeos de tipo/estilo.
 */
@DisplayName("ShowHiddenFilesAction tests")
class ShowHiddenFilesActionTest {

    @Test
    @DisplayName("Builder should set message, tooltip, imageDescriptor and style")
    void builderShouldSetMessageTooltipImageDescriptorAndStyle() {
        ImageDescriptor descriptor = ImageDescriptor.getMissingImageDescriptor();

        ShowHiddenFilesAction action = ShowHiddenFilesAction.builder().message("Show hidden files")
                .tooltip("Show hidden files tooltip").imageDescriptor(descriptor).style(IAction.AS_CHECK_BOX).build();

        assertEquals("Show hidden files", action.getText());
        assertEquals("Show hidden files tooltip", action.getToolTipText());
        assertSame(descriptor, action.getImageDescriptor());
        assertEquals(IAction.AS_CHECK_BOX, action.getStyle());
    }

    @Test
    @DisplayName("Builder should leave the action enabled and unchecked by default")
    void builderShouldLeaveActionEnabledAndUncheckedByDefault() {
        ShowHiddenFilesAction action = ShowHiddenFilesAction.builder().style(Action.AS_CHECK_BOX).build();

        assertTrue(action.isEnabled());
        assertFalse(action.isChecked());
    }

    @Test
    @DisplayName("Should be an instance of JISOCreatorBaseAction")
    void shouldBeInstanceOfJISOCreatorBaseAction() {
        ShowHiddenFilesAction action = ShowHiddenFilesAction.builder().style(Action.AS_CHECK_BOX).build();

        assertTrue(action instanceof JISOCreatorBaseAction);
    }

    @Test
    @DisplayName("Each builder call should produce an independent instance")
    void eachBuilderCallShouldProduceIndependentInstance() {
        ShowHiddenFilesAction action = ShowHiddenFilesAction.builder().message("First").style(Action.AS_CHECK_BOX)
                .build();
        ShowHiddenFilesAction anotherAction = ShowHiddenFilesAction.builder().message("Second")
                .style(Action.AS_CHECK_BOX).build();

        assertNotSame(action, anotherAction);
        assertEquals("First", action.getText());
        assertEquals("Second", anotherAction.getText());
    }
}
