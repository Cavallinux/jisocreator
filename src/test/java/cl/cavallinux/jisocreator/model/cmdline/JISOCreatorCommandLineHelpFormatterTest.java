package cl.cavallinux.jisocreator.model.cmdline;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.help.TableDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.gui.i18n.CommandLineMessages;

@DisplayName("JISOCreatorCommandLineHelpFormatter Tests")
class JISOCreatorCommandLineHelpFormatterTest {

    private JISOCreatorCommandLineHelpFormatter formatter;
    private JISOCreatorCommandLineParser parser;
    private Options options;

    @BeforeEach
    void setUp() {
        formatter = new JISOCreatorCommandLineHelpFormatter();
        parser = JISOCreatorCommandLineParser.builder().build();
        options = parser.buildOptions();
    }

    @Test
    @DisplayName("Should return a non-null table definition for the given options")
    void shouldReturnNonNullTableDefinition() {
        TableDefinition tableDefinition = formatter.getTableDefinition(options.getOptions());
        assertNotNull(tableDefinition);
    }

    @Test
    @DisplayName("Should override table caption with the i18n options table title message")
    void shouldOverrideTableCaptionWithI18nTitle() {
        TableDefinition tableDefinition = formatter.getTableDefinition(options.getOptions());
        assertEquals(CommandLineMessages.commandLineIsoOptionsTableTitleMessage, tableDefinition.caption());
    }

    @Test
    @DisplayName("Should override column headers with i18n Option and Description messages")
    void shouldOverrideColumnHeadersWithI18nMessages() {
        TableDefinition tableDefinition = formatter.getTableDefinition(options.getOptions());
        assertNotNull(tableDefinition.headers());
        assertEquals(2, tableDefinition.headers().size());
        assertEquals(CommandLineMessages.commandLineIsoOptionsTableOptionColumnMessage,
                tableDefinition.headers().get(0));
        assertEquals(CommandLineMessages.commandLineIsoOptionsTableDescriptionColumnMessage,
                tableDefinition.headers().get(1));
    }

    @Test
    @DisplayName("Should preserve column text styles from the parent formatter")
    void shouldPreserveColumnTextStylesFromParent() {
        TableDefinition tableDefinition = formatter.getTableDefinition(options.getOptions());
        assertNotNull(tableDefinition.columnTextStyles());
    }

    @Test
    @DisplayName("Should preserve table rows from the parent formatter")
    void shouldPreserveTableRowsFromParent() {
        TableDefinition tableDefinition = formatter.getTableDefinition(options.getOptions());
        assertNotNull(tableDefinition.rows());
    }

    @Test
    @DisplayName("Should print help without throwing an exception")
    void shouldPrintHelpWithoutThrowingException() {
        assertDoesNotThrow(
                () -> formatter.printHelp("jisocreator", "header", options, "footer", true));
    }
}
