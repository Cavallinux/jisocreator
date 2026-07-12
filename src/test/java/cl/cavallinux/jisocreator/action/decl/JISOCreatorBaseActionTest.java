package cl.cavallinux.jisocreator.action.decl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("JISOCreatorBaseAction constructor tests")
class JISOCreatorBaseActionTest {
    @Test
    @DisplayName("Should initialize with default constructor")
    void shouldInitializeWithDefaultConstructor() {
        ExposedAction action = ExposedAction.defaultCtor();

        assertNull(action.getText());
        assertNull(action.getToolTipText());
        assertNull(action.getImageDescriptor());
    }

    @Test
    @DisplayName("Should initialize with text-only constructor")
    void shouldInitializeWithTextOnlyConstructor() {
        ExposedAction action = ExposedAction.textCtor("Action label");

        assertEquals("Action label", action.getText());
        assertNull(action.getToolTipText());
        assertNull(action.getImageDescriptor());
    }

    @Test
    @DisplayName("Should initialize with text tooltip and image constructor")
    void shouldInitializeWithTextTooltipAndImageConstructor() {
        ImageDescriptor descriptor = ImageDescriptor.getMissingImageDescriptor();
        ExposedAction action = ExposedAction.fullCtor("Action label", "Tooltip", descriptor);

        assertEquals("Action label", action.getText());
        assertEquals("Tooltip", action.getToolTipText());
        assertSame(descriptor, action.getImageDescriptor());
    }

    @Test
    @DisplayName("Should initialize with style-aware constructor")
    void shouldInitializeWithStyleAwareConstructor() {
        ImageDescriptor descriptor = ImageDescriptor.getMissingImageDescriptor();
        ExposedAction action = ExposedAction.styledCtor("Action label", "Tooltip", descriptor, Action.AS_CHECK_BOX);

        assertEquals("Action label", action.getText());
        assertEquals("Tooltip", action.getToolTipText());
        assertSame(descriptor, action.getImageDescriptor());
        assertEquals(Action.AS_CHECK_BOX, action.getStyle());
    }

    private static final class ExposedAction extends JISOCreatorBaseAction {
        private ExposedAction() {
            super();
        }

        private ExposedAction(String message) {
            super(message);
        }

        private ExposedAction(String message, String tooltip, ImageDescriptor imageDescriptor) {
            super(message, tooltip, imageDescriptor);
        }

        private ExposedAction(String message, String tooltip, ImageDescriptor imageDescriptor, int style) {
            super(message, tooltip, imageDescriptor, style);
        }

        private static ExposedAction defaultCtor() {
            return new ExposedAction();
        }

        private static ExposedAction textCtor(String message) {
            return new ExposedAction(message);
        }

        private static ExposedAction fullCtor(String message, String tooltip, ImageDescriptor imageDescriptor) {
            return new ExposedAction(message, tooltip, imageDescriptor);
        }

        private static ExposedAction styledCtor(String message, String tooltip, ImageDescriptor imageDescriptor,
                int style) {
            return new ExposedAction(message, tooltip, imageDescriptor, style);
        }
    }
}
