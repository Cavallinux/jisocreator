package cl.cavallinux.jisocreator.instances;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("JISOCreatorThemeOptions tests")
class JISOCreatorThemeOptionsTest {
    @Test
    @DisplayName("Should declare AUTO, LIGHT and DARK with expected preference values")
    void shouldDeclareAutoLightAndDarkWithExpectedValues() {
        List<String> expectedValues = List.of("AUTO", "LIGHT", "DARK");
        List<String> actualValues = Arrays.stream(JISOCreatorThemeOptions.values())
                .map(JISOCreatorThemeOptions::getThemeModeValue).toList();

        assertEquals(expectedValues, actualValues);
    }

    @Test
    @DisplayName("Should expose non-empty localized labels for each theme option")
    void shouldExposeNonEmptyLocalizedLabelsForEachThemeOption() {
        for (JISOCreatorThemeOptions option : JISOCreatorThemeOptions.values()) {
            assertFalse(option.getThemeModeText().isBlank());
        }
    }
}
