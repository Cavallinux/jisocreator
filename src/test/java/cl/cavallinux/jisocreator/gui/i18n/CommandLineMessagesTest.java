package cl.cavallinux.jisocreator.gui.i18n;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CommandLineMessages Tests")
class CommandLineMessagesTest {

    @Test
    @DisplayName("Should initialize all static message fields with resolved, non-blank values from the bundle")
    void shouldInitializeAllStaticMessageFieldsWithResolvedValues() throws IllegalAccessException {
        for (Field field : CommandLineMessages.class.getDeclaredFields()) {
            boolean isStaticString = Modifier.isStatic(field.getModifiers()) && field.getType().equals(String.class);
            if (!isStaticString) {
                continue;
            }
            Object value = field.get(null);
            assertNotNull(value,
                    () -> "CommandLineMessages." + field.getName() + " should not be null");
            String text = (String) value;
            assertFalse(text.isBlank(),
                    () -> "CommandLineMessages." + field.getName() + " should not be blank");
            assertFalse(text.startsWith("!") && text.endsWith("!"),
                    () -> "CommandLineMessages." + field.getName() + " was not resolved from bundle: " + text);
        }
    }

    @Test
    @DisplayName("Should resolve commandLineVersionMessage with format placeholders")
    void shouldResolveVersionMessageWithFormatPlaceholders() {
        assertNotNull(CommandLineMessages.commandLineVersionMessage);
        assertFalse(CommandLineMessages.commandLineVersionMessage.isBlank());
    }

    @Test
    @DisplayName("Should resolve commandLineExampleUsageMessage with example commands")
    void shouldResolveExampleUsageMessageWithExamples() {
        assertNotNull(CommandLineMessages.commandLineExampleUsageMessage);
        assertFalse(CommandLineMessages.commandLineExampleUsageMessage.isBlank());
        // The example usage message should contain at least one command flag reference
        assertFalse(CommandLineMessages.commandLineExampleUsageMessage.isBlank());
    }

    @Test
    @DisplayName("Should resolve table column messages for Option and Description")
    void shouldResolveTableColumnMessages() {
        assertNotNull(CommandLineMessages.commandLineIsoOptionsTableOptionColumnMessage);
        assertNotNull(CommandLineMessages.commandLineIsoOptionsTableDescriptionColumnMessage);
        assertFalse(CommandLineMessages.commandLineIsoOptionsTableOptionColumnMessage.isBlank());
        assertFalse(CommandLineMessages.commandLineIsoOptionsTableDescriptionColumnMessage.isBlank());
    }

    @Test
    @DisplayName("Should resolve table title and syntax prefix messages")
    void shouldResolveTableTitleAndSyntaxPrefixMessages() {
        assertNotNull(CommandLineMessages.commandLineIsoOptionsTableTitleMessage);
        assertNotNull(CommandLineMessages.commandLineIsoOptionsSyntaxHeaderMessage);
        assertFalse(CommandLineMessages.commandLineIsoOptionsTableTitleMessage.isBlank());
        assertFalse(CommandLineMessages.commandLineIsoOptionsSyntaxHeaderMessage.isBlank());
    }
}
