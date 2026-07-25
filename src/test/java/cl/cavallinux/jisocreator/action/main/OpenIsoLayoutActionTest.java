package cl.cavallinux.jisocreator.action.main;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.action.decl.IFileManagementAction;
import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;

/**
 * Tests para {@link OpenIsoLayoutAction}.
 *
 * <p>{@code run()}/{@code run(IProgressMonitor)} dependen de {@code Display.getDefault()}
 * y de {@code GUIManager.INSTANCE}, por lo que no son invocables de forma
 * headless. Sin embargo, los metodos default heredados de
 * {@link IFileManagementAction} ({@code obtainFileFilterNames},
 * {@code obtainFileDialogExtensions}) son logica pura y si son testeables
 * directamente sobre la instancia.
 */
@DisplayName("OpenIsoLayoutAction tests")
class OpenIsoLayoutActionTest {

    @Test
    @DisplayName("Builder should set message, tooltip and imageDescriptor")
    void builderShouldSetMessageTooltipAndImageDescriptor() {
        ImageDescriptor descriptor = ImageDescriptor.getMissingImageDescriptor();

        OpenIsoLayoutAction action = OpenIsoLayoutAction.builder().message("Open").tooltip("Open tooltip")
                .imageDescriptor(descriptor).build();

        assertEquals("Open", action.getText());
        assertEquals("Open tooltip", action.getToolTipText());
        assertSame(descriptor, action.getImageDescriptor());
    }

    @Test
    @DisplayName("Should implement JISOCreatorBaseAction, IRunnableWithProgress and IFileManagementAction")
    void shouldImplementExpectedTypes() {
        OpenIsoLayoutAction action = OpenIsoLayoutAction.builder().build();

        assertTrue(action instanceof JISOCreatorBaseAction);
        assertTrue(action instanceof IRunnableWithProgress);
        assertTrue(action instanceof IFileManagementAction);
    }

    @Test
    @DisplayName("obtainFileFilterNames should wrap the extension in a single-element array")
    void obtainFileFilterNamesShouldWrapExtensionInArray() {
        OpenIsoLayoutAction action = OpenIsoLayoutAction.builder().build();

        assertArrayEquals(new String[] { "*.xml" }, action.obtainFileFilterNames("*.xml"));
    }

    @Test
    @DisplayName("obtainFileDialogExtensions should prefix the extension with a wildcard")
    void obtainFileDialogExtensionsShouldPrefixExtensionWithWildcard() {
        OpenIsoLayoutAction action = OpenIsoLayoutAction.builder().build();

        assertArrayEquals(new String[] { "*.xml" }, action.obtainFileDialogExtensions(".xml"));
    }
}
