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
 * Tests para {@link SaveAsXMLAction}.
 *
 * <p>{@code run()}/{@code run(IProgressMonitor)} dependen de
 * {@code GUIManager.INSTANCE} y de {@code Display.getDefault()}, por lo que no
 * son invocables de forma headless. Se cubren la construccion via builder y
 * los metodos default puros heredados de {@link IFileManagementAction}, igual
 * que en {@code OpenIsoLayoutActionTest}.
 */
@DisplayName("SaveAsXMLAction tests")
class SaveAsXMLActionTest {

    @Test
    @DisplayName("Builder should set message, tooltip and imageDescriptor")
    void builderShouldSetMessageTooltipAndImageDescriptor() {
        ImageDescriptor descriptor = ImageDescriptor.getMissingImageDescriptor();

        SaveAsXMLAction action = SaveAsXMLAction.builder().message("Save as XML").toolTip("Save as XML tooltip")
                .imageDescriptor(descriptor).build();

        assertEquals("Save as XML", action.getText());
        assertEquals("Save as XML tooltip", action.getToolTipText());
        assertSame(descriptor, action.getImageDescriptor());
    }

    @Test
    @DisplayName("Should implement JISOCreatorBaseAction, IRunnableWithProgress and IFileManagementAction")
    void shouldImplementExpectedTypes() {
        SaveAsXMLAction action = SaveAsXMLAction.builder().build();

        assertTrue(action instanceof JISOCreatorBaseAction);
        assertTrue(action instanceof IRunnableWithProgress);
        assertTrue(action instanceof IFileManagementAction);
    }

    @Test
    @DisplayName("obtainFileFilterNames should wrap the extension in a single-element array")
    void obtainFileFilterNamesShouldWrapExtensionInArray() {
        SaveAsXMLAction action = SaveAsXMLAction.builder().build();

        assertArrayEquals(new String[] { "*.xml" }, action.obtainFileFilterNames("*.xml"));
    }

    @Test
    @DisplayName("obtainFileDialogExtensions should prefix the extension with a wildcard")
    void obtainFileDialogExtensionsShouldPrefixExtensionWithWildcard() {
        SaveAsXMLAction action = SaveAsXMLAction.builder().build();

        assertArrayEquals(new String[] { "*.xml" }, action.obtainFileDialogExtensions(".xml"));
    }
}
