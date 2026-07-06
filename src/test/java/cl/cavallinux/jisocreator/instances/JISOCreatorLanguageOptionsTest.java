package cl.cavallinux.jisocreator.instances;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("JISOCreatorLanguageOptions tests")
class JISOCreatorLanguageOptionsTest {
    @Test
    @DisplayName("Should map language options to expected locales")
    void shouldMapLanguageOptionsToExpectedLocales() {
        assertEquals(Locale.ENGLISH, JISOCreatorLanguageOptions.ENGLISH.getLanguageLocale());
        assertEquals("es", JISOCreatorLanguageOptions.SPANISH.getLanguageLocale().getLanguage());
    }

    @Test
    @DisplayName("Should expose non-empty localized labels for each language option")
    void shouldExposeNonEmptyLocalizedLabelsForEachLanguageOption() {
        for (JISOCreatorLanguageOptions option : JISOCreatorLanguageOptions.values()) {
            assertFalse(option.getLanguageText().isBlank());
        }
    }
}
