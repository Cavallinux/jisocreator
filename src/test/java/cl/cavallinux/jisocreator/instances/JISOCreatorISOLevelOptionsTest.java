package cl.cavallinux.jisocreator.instances;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("JISOCreatorISOLevelOptions tests")
class JISOCreatorISOLevelOptionsTest {
    @Test
    @DisplayName("Should declare all four ISO levels with expected values")
    void shouldDeclareAllFourIsoLevelsWithExpectedValues() {
        List<String> expectedValues = List.of("1", "2", "3", "4");
        List<String> actualValues = Arrays.stream(JISOCreatorISOLevelOptions.values())
                .map(JISOCreatorISOLevelOptions::getIsoLevelValue)
                .toList();

        assertEquals(expectedValues, actualValues);
    }

    @Test
    @DisplayName("Should expose non-empty localized labels for each ISO level")
    void shouldExposeNonEmptyLocalizedLabelsForEachIsoLevel() {
        for (JISOCreatorISOLevelOptions option : JISOCreatorISOLevelOptions.values()) {
            assertFalse(option.getIsoLevelText().isBlank());
        }
    }
}
