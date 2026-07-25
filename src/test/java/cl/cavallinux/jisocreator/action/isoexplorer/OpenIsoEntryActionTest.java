package cl.cavallinux.jisocreator.action.isoexplorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.resource.ImageDescriptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;

/**
 * Tests para {@link OpenIsoEntryAction}.
 *
 * <p>{@code run()} nunca es invocado en estos tests: su rama de archivo llama
 * a {@code OSExplorer.launch(Path)} (que delega en {@code Program.launch},
 * una API nativa de SWT que abriria de verdad una aplicacion externa) y su
 * rama de directorio depende de {@code GUIManager.INSTANCE.getMainWindow()}.
 * Ninguna de las dos ramas es apta para un test unitario headless, por lo que
 * se cubre unicamente la construccion via builder, el accesor/mutador Lombok
 * de {@code node} y los chequeos de tipo.
 */
@DisplayName("OpenIsoEntryAction tests")
class OpenIsoEntryActionTest {

    @Test
    @DisplayName("Builder should set message, tooltip and imageDescriptor")
    void builderShouldSetMessageTooltipAndImageDescriptor() {
        ImageDescriptor descriptor = ImageDescriptor.getMissingImageDescriptor();

        OpenIsoEntryAction action = OpenIsoEntryAction.builder().message("Open").tooltip("Open tooltip")
                .imageDescriptor(descriptor).build();

        assertEquals("Open", action.getText());
        assertEquals("Open tooltip", action.getToolTipText());
        assertSame(descriptor, action.getImageDescriptor());
    }

    @Test
    @DisplayName("Builder should initialise the action as disabled")
    void builderShouldInitialiseActionAsDisabled() {
        OpenIsoEntryAction action = OpenIsoEntryAction.builder().build();

        assertFalse(action.isEnabled());
    }

    @Test
    @DisplayName("Node should be null by default and settable via Lombok accessor")
    void nodeShouldBeNullByDefaultAndSettable() {
        OpenIsoEntryAction action = OpenIsoEntryAction.builder().build();
        assertNull(action.getNode());

        ITreeNode node = mock(ITreeNode.class);
        action.setNode(node);

        assertSame(node, action.getNode());
    }

    @Test
    @DisplayName("Should be an instance of JISOCreatorBaseAction")
    void shouldBeInstanceOfJISOCreatorBaseAction() {
        OpenIsoEntryAction action = OpenIsoEntryAction.builder().build();

        assertTrue(action instanceof JISOCreatorBaseAction);
    }

    @Test
    @DisplayName("Each builder call should produce an independent instance")
    void eachBuilderCallShouldProduceIndependentInstance() {
        OpenIsoEntryAction action = OpenIsoEntryAction.builder().message("First").build();
        OpenIsoEntryAction anotherAction = OpenIsoEntryAction.builder().message("Second").build();

        assertNotSame(action, anotherAction);
        assertEquals("First", action.getText());
        assertEquals("Second", anotherAction.getText());
    }
}
