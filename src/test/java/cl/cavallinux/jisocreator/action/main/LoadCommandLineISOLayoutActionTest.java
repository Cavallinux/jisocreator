package cl.cavallinux.jisocreator.action.main;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;

/**
 * Tests para {@link LoadCommandLineISOLayoutAction}.
 *
 * <p>{@code run(String)} depende de {@code IOManager.INSTANCE} (parseo xml real)
 * y de {@code GUIManager.INSTANCE.getMainWindow()} (arbol de exploracion ISO
 * real), por lo que no es invocable de forma headless. Se cubre la
 * construccion via builder, unico comportamiento independiente de la GUI.
 */
@DisplayName("LoadCommandLineISOLayoutAction tests")
class LoadCommandLineISOLayoutActionTest {

    @Test
    @DisplayName("Builder should create a non-null instance")
    void builderShouldCreateNonNullInstance() {
        LoadCommandLineISOLayoutAction action = LoadCommandLineISOLayoutAction.builder().build();

        assertNotNull(action);
    }

    @Test
    @DisplayName("Should be an instance of JISOCreatorBaseAction")
    void shouldBeInstanceOfJISOCreatorBaseAction() {
        LoadCommandLineISOLayoutAction action = LoadCommandLineISOLayoutAction.builder().build();

        assertTrue(action instanceof JISOCreatorBaseAction);
    }

    @Test
    @DisplayName("Each builder call should produce an independent instance")
    void eachBuilderCallShouldProduceIndependentInstance() {
        LoadCommandLineISOLayoutAction action = LoadCommandLineISOLayoutAction.builder().build();
        LoadCommandLineISOLayoutAction anotherAction = LoadCommandLineISOLayoutAction.builder().build();

        assertNotSame(action, anotherAction);
    }
}
