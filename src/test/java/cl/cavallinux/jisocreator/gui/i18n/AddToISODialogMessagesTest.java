package cl.cavallinux.jisocreator.gui.i18n;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AddToISODialogMessages Tests")
class AddToISODialogMessagesTest {

    @Test
    @DisplayName("Should initialize all static message fields with resolved, non-blank values from the bundle")
    void shouldInitializeAllStaticMessageFieldsWithResolvedValues() throws IllegalAccessException {
        for (Field field : AddToISODialogMessages.class.getDeclaredFields()) {
            boolean isStaticString = Modifier.isStatic(field.getModifiers()) && field.getType().equals(String.class);
            if (!isStaticString) {
                continue;
            }
            Object value = field.get(null);
            assertNotNull(value, () -> "AddToISODialogMessages." + field.getName() + " should not be null");
            String text = (String) value;
            assertFalse(text.isBlank(), () -> "AddToISODialogMessages." + field.getName() + " should not be blank");
            assertFalse(text.startsWith("!") && text.endsWith("!"),
                    () -> "AddToISODialogMessages." + field.getName() + " was not resolved from bundle: " + text);
        }
    }

    @Test
    @DisplayName("Should expose non-blank window title, static info and button labels")
    void shouldExposeNonBlankMessages() {
        assertNotNull(AddToISODialogMessages.addToIsoDialogWindowTitle);
        assertNotNull(AddToISODialogMessages.addToIsoDialogStaticInfo);
        assertNotNull(AddToISODialogMessages.addToIsoDialogOKButtonLabel);
        assertNotNull(AddToISODialogMessages.addToIsoDialogOKCancelLabel);

        assertFalse(AddToISODialogMessages.addToIsoDialogWindowTitle.isBlank());
        assertFalse(AddToISODialogMessages.addToIsoDialogStaticInfo.isBlank());
        assertFalse(AddToISODialogMessages.addToIsoDialogOKButtonLabel.isBlank());
        assertFalse(AddToISODialogMessages.addToIsoDialogOKCancelLabel.isBlank());
    }

    @Test
    @DisplayName("Should load the addtoisodialog bundle for english and spanish locales with all expected keys")
    void shouldLoadBundleForEnglishAndSpanishLocales() {
        ResourceBundle english = ResourceBundle.getBundle(INLSBundleMessages.ADDTOISODIALOG_BUNDLE_MESSAGE,
                Locale.ENGLISH);
        ResourceBundle spanish = ResourceBundle.getBundle(INLSBundleMessages.ADDTOISODIALOG_BUNDLE_MESSAGE,
                Locale.of("es"));

        assertNotNull(english);
        assertNotNull(spanish);
        assertFalse(english.keySet().isEmpty());
        assertFalse(spanish.keySet().isEmpty());

        assertNotNull(english.getString("addToIsoDialogWindowTitle"));
        assertNotNull(spanish.getString("addToIsoDialogWindowTitle"));
    }
}
