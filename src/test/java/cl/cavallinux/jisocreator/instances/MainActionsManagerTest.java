package cl.cavallinux.jisocreator.instances;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.action.main.MainAction;

@DisplayName("MainActionsManager Tests")
class MainActionsManagerTest {

    @Test
    @DisplayName("Should declare exactly one enum constant: MAINACTION")
    void shouldDeclareExactlyOneConstant() {
        assertEquals(1, MainActionsManager.values().length);
    }

    @Test
    @DisplayName("Should expose a non-null action for MAINACTION")
    void shouldExposeNonNullActionForMainAction() {
        assertNotNull(MainActionsManager.MAINACTION.getAction());
    }

    @Test
    @DisplayName("Should expose a MainAction instance for MAINACTION")
    void shouldExposeMainActionInstanceForMainAction() {
        assertTrue(MainActionsManager.MAINACTION.getAction() instanceof MainAction);
    }

    @Test
    @DisplayName("Should return the same MainAction instance on every access (enum singleton)")
    void shouldReturnSameInstanceOnEveryAccess() {
        assertSame(MainActionsManager.MAINACTION.getAction(), MainActionsManager.MAINACTION.getAction());
    }

    @Test
    @DisplayName("Should initialize MAINACTION with an empty layout file path")
    void shouldInitializeMainActionWithEmptyLayoutFilePath() {
        MainAction action = (MainAction) MainActionsManager.MAINACTION.getAction();
        assertNotNull(action.getLayoutFilePath());
        assertTrue(action.getLayoutFilePath().isEmpty());
    }

    @Test
    @DisplayName("Should initialize MAINACTION with a non-null parser from CommandLineParserManager")
    void shouldInitializeMainActionWithNonNullParser() {
        MainAction action = (MainAction) MainActionsManager.MAINACTION.getAction();
        assertNotNull(action.getParser());
        assertSame(CommandLineParserManager.INSTANCE.getParser(), action.getParser());
    }
}
