package cl.cavallinux.jisocreator.action.osexplorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;

/**
 * Tests para {@link OpenAction}.
 *
 * <p>{@code run()} nunca es invocado en estos tests: su rama de archivo llama
 * a {@code OSExplorer.launch(Path)} (que delega en {@code Program.launch},
 * una API nativa de SWT que abriria de verdad una aplicacion externa) y su
 * rama de directorio depende de {@code GUIManager.INSTANCE.getMainWindow()}.
 * Ninguna de las dos ramas es apta para un test unitario headless, por lo que
 * se cubre unicamente la construccion via builder, el accesor/mutador Lombok
 * de {@code file} y los chequeos de tipo, siguiendo el mismo patron aplicado
 * a {@code OpenIsoEntryAction} en el Grupo 4.
 */
@DisplayName("OpenAction tests")
class OpenActionTest {

    @Test
    @DisplayName("Builder should set message, tooltip and imageDescriptor")
    void builderShouldSetMessageTooltipAndImageDescriptor() {
        ImageDescriptor descriptor = ImageDescriptor.getMissingImageDescriptor();

        OpenAction action = OpenAction.builder().message("Open").tooltip("Open tooltip").imageDescriptor(descriptor)
                .build();

        assertEquals("Open", action.getText());
        assertEquals("Open tooltip", action.getToolTipText());
        assertSame(descriptor, action.getImageDescriptor());
    }

    @Test
    @DisplayName("Builder should initialise the action as disabled")
    void builderShouldInitialiseActionAsDisabled() {
        OpenAction action = OpenAction.builder().build();

        assertFalse(action.isEnabled());
    }

    @Test
    @DisplayName("File should be null by default and settable via Lombok accessor")
    void fileShouldBeNullByDefaultAndSettable() {
        OpenAction action = OpenAction.builder().build();
        assertNull(action.getFile());

        File file = new File("/tmp");
        action.setFile(file);

        assertSame(file, action.getFile());
    }

    @Test
    @DisplayName("Should be an instance of JISOCreatorBaseAction")
    void shouldBeInstanceOfJISOCreatorBaseAction() {
        OpenAction action = OpenAction.builder().build();

        assertTrue(action instanceof JISOCreatorBaseAction);
    }

    @Test
    @DisplayName("Each builder call should produce an independent instance")
    void eachBuilderCallShouldProduceIndependentInstance() {
        OpenAction action = OpenAction.builder().message("First").build();
        OpenAction anotherAction = OpenAction.builder().message("Second").build();

        assertNotSame(action, anotherAction);
        assertEquals("First", action.getText());
        assertEquals("Second", anotherAction.getText());
    }
}
