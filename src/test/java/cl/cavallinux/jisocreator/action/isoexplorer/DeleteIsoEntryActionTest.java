package cl.cavallinux.jisocreator.action.isoexplorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;

/**
 * Tests para {@link DeleteIsoEntryAction}.
 *
 * <p>{@code run()}/{@code run(IProgressMonitor)} dependen de
 * {@code GUIManager.INSTANCE.getMainWindow()} y de {@code Display.getDefault()},
 * por lo que no son invocables de forma headless. Se cubren la construccion
 * via builder (incluyendo el estado inicial {@code setEnabled(false)} fijado
 * en el constructor) y los chequeos de tipo, siguiendo el mismo patron que
 * las acciones del Grupo 3 (p.e. {@code AboutActionTest}).
 */
@DisplayName("DeleteIsoEntryAction tests")
class DeleteIsoEntryActionTest {

    @Test
    @DisplayName("Builder should set message, tooltip and imageDescriptor")
    void builderShouldSetMessageTooltipAndImageDescriptor() {
        ImageDescriptor descriptor = ImageDescriptor.getMissingImageDescriptor();

        DeleteIsoEntryAction action = DeleteIsoEntryAction.builder().message("Delete").tooltip("Delete tooltip")
                .imageDescriptor(descriptor).build();

        assertEquals("Delete", action.getText());
        assertEquals("Delete tooltip", action.getToolTipText());
        assertSame(descriptor, action.getImageDescriptor());
    }

    @Test
    @DisplayName("Builder should initialise the action as disabled")
    void builderShouldInitialiseActionAsDisabled() {
        DeleteIsoEntryAction action = DeleteIsoEntryAction.builder().build();

        assertFalse(action.isEnabled());
    }

    @Test
    @DisplayName("Should implement JISOCreatorBaseAction and IRunnableWithProgress")
    void shouldImplementExpectedTypes() {
        DeleteIsoEntryAction action = DeleteIsoEntryAction.builder().build();

        assertTrue(action instanceof JISOCreatorBaseAction);
        assertTrue(action instanceof IRunnableWithProgress);
    }

    @Test
    @DisplayName("Each builder call should produce an independent instance")
    void eachBuilderCallShouldProduceIndependentInstance() {
        DeleteIsoEntryAction action = DeleteIsoEntryAction.builder().message("First").build();
        DeleteIsoEntryAction anotherAction = DeleteIsoEntryAction.builder().message("Second").build();

        assertNotSame(action, anotherAction);
        assertEquals("First", action.getText());
        assertEquals("Second", anotherAction.getText());
    }
}
