package cl.cavallinux.jisocreator.gui.i18n;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Messages bundle tests")
class MessagesBundleTest {
    private static final List<String> BASE_BUNDLES = List.of(
            INLSBundleMessages.MAIN_WINDOW_BUNDLE_MESSAGE,
            INLSBundleMessages.MAIN_ACTIONS_BUNDLE_MESSAGE,
            INLSBundleMessages.OSEXPLORER_BUNDLE_MESSAGE,
            INLSBundleMessages.ISOEXPLORER_BUNDLE_MESSAGE,
            INLSBundleMessages.SHOWISOINFO_BUNDLE_MESSAGE,
            INLSBundleMessages.PREFERENCEDIALOG_BUNDLE_MESSAGE,
            INLSBundleMessages.ABOUTDIALOG_BUNDLE_MESSAGE);

    @Test
    @DisplayName("Should load all message bundles for english and spanish locales")
    void shouldLoadAllMessageBundlesForEnglishAndSpanishLocales() {
        for (String baseBundle : BASE_BUNDLES) {
            ResourceBundle english = ResourceBundle.getBundle(baseBundle, Locale.ENGLISH);
            ResourceBundle spanish = ResourceBundle.getBundle(baseBundle, Locale.of("es"));
            assertNotNull(english);
            assertNotNull(spanish);
            assertFalse(english.keySet().isEmpty());
            assertFalse(spanish.keySet().isEmpty());
        }
    }

    @Test
    @DisplayName("Should initialize all static message fields with resolved values")
    void shouldInitializeAllStaticMessageFieldsWithResolvedValues() throws IllegalAccessException {
        List<Class<?>> messageClasses = List.of(
                MainWindowMessages.class,
                MainActionsMessages.class,
                OSExplorerMessages.class,
                IsoExplorerMessages.class,
                ShowIsoInformationDialogMessages.class,
                PreferenceDialogMessages.class,
                AboutDialogMessages.class);

        for (Class<?> messageClass : messageClasses) {
            for (Field field : messageClass.getDeclaredFields()) {
                boolean isStaticString = Modifier.isStatic(field.getModifiers()) && field.getType().equals(String.class);
                if (!isStaticString) {
                    continue;
                }
                Object value = field.get(null);
                assertNotNull(value, () -> messageClass.getSimpleName() + "." + field.getName() + " should not be null");
                String text = (String) value;
                assertFalse(text.isBlank(),
                        () -> messageClass.getSimpleName() + "." + field.getName() + " should not be blank");
                assertTrue(!(text.startsWith("!") && text.endsWith("!")),
                        () -> messageClass.getSimpleName() + "." + field.getName() + " was not resolved from bundle");
            }
        }
    }
}
