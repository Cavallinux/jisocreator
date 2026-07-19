package cl.cavallinux.jisocreator.action.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests para {@link SaveAsIsoAction}.
 *
 * <p>El constructor {@code @Builder protected SaveAsIsoAction()} fue añadido en el
 * commit {@code d68a1a5} para permitir la instanciación desde
 * {@code MainActionsManager.SAVEASISOACTION} sin necesidad de pasar descriptores
 * de imagen o etiquetas propias de la GUI.
 */
@DisplayName("SaveAsIsoAction tests")
class SaveAsIsoActionTest {

    private SaveAsIsoAction action;

    @BeforeEach
    void setUp() {
        // Utiliza el constructor protegido añadido en d68a1a5
        action = SaveAsIsoAction.builder().build();
    }

    // -------------------------------------------------------------------------
    // Estado inicial — builder sin argumentos (commit d68a1a5)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Builder default should initialise inputXMLLayoutFile as empty string")
    void shouldInitialiseInputXmlLayoutFileAsEmptyString() {
        assertNotNull(action.getInputXMLLayoutFile());
        assertEquals(StringUtils.EMPTY, action.getInputXMLLayoutFile());
    }

    @Test
    @DisplayName("Builder default should initialise outputISOFile as empty string")
    void shouldInitialiseOutputIsoFileAsEmptyString() {
        assertNotNull(action.getOutputISOFile());
        assertEquals(StringUtils.EMPTY, action.getOutputISOFile());
    }

    @Test
    @DisplayName("Builder default should initialise commandLineMode as false")
    void shouldInitialiseCommandLineModeAsFalse() {
        assertFalse(action.isCommandLineMode());
    }

    // -------------------------------------------------------------------------
    // Setters (usados en MainAction.handleCommandLine)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("setInputXMLLayoutFile should store and expose the supplied path")
    void shouldStoreAndExposeInputXMLLayoutFile() {
        action.setInputXMLLayoutFile("/home/user/layout.xml");
        assertEquals("/home/user/layout.xml", action.getInputXMLLayoutFile());
    }

    @Test
    @DisplayName("setOutputISOFile should store and expose the supplied path")
    void shouldStoreAndExposeOutputISOFile() {
        action.setOutputISOFile("/tmp/my-disk.iso");
        assertEquals("/tmp/my-disk.iso", action.getOutputISOFile());
    }

    @Test
    @DisplayName("setCommandLineMode should switch flag to true")
    void shouldSwitchCommandLineModeToTrue() {
        action.setCommandLineMode(true);
        assertTrue(action.isCommandLineMode());
    }

    @Test
    @DisplayName("setCommandLineMode should switch flag back to false")
    void shouldSwitchCommandLineModeBackToFalse() {
        action.setCommandLineMode(true);
        action.setCommandLineMode(false);
        assertFalse(action.isCommandLineMode());
    }

    // -------------------------------------------------------------------------
    // Múltiples instancias independientes
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Each builder call should produce an independent instance")
    void eachBuilderCallShouldProduceIndependentInstance() {
        SaveAsIsoAction anotherAction = SaveAsIsoAction.builder().build();
        assertNotSame(action, anotherAction);

        action.setInputXMLLayoutFile("layout-A.xml");
        anotherAction.setInputXMLLayoutFile("layout-B.xml");

        assertEquals("layout-A.xml", action.getInputXMLLayoutFile());
        assertEquals("layout-B.xml", anotherAction.getInputXMLLayoutFile());
    }
}
