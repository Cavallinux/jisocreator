package cl.cavallinux.jisocreator.instances;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.cli.Option;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CommandLineOptionsManager Tests")
class CommandLineOptionsManagerTest {

    @Test
    @DisplayName("Should define the LOAD option with short 'l' and long 'load', requiring an argument")
    void testLoadOption() {
        Option option = CommandLineOptionsManager.LOAD.getOption();

        assertEquals("l", option.getOpt());
        assertEquals("load", option.getLongOpt());
        assertTrue(option.hasArg());
        assertEquals("xmllayout", option.getArgName());
    }

    @Test
    @DisplayName("Should define the HELP option with short 'h' and long 'help', without arguments")
    void testHelpOption() {
        Option option = CommandLineOptionsManager.HELP.getOption();

        assertEquals("h", option.getOpt());
        assertEquals("help", option.getLongOpt());
        assertFalse(option.hasArg());
    }

    @Test
    @DisplayName("Should define the VERSION option with short 'v' and long 'version', without arguments")
    void testVersionOption() {
        Option option = CommandLineOptionsManager.VERSION.getOption();

        assertEquals("v", option.getOpt());
        assertEquals("version", option.getLongOpt());
        assertFalse(option.hasArg());
    }

    @Test
    @DisplayName("Should define the LICENSE option with short 'L' and long 'license', without arguments")
    void testLicenseOption() {
        Option option = CommandLineOptionsManager.LICENSE.getOption();

        assertEquals("L", option.getOpt());
        assertEquals("license", option.getLongOpt());
        assertFalse(option.hasArg());
    }

    @Test
    @DisplayName("Should define the ISOINPUT option with short 'i' and long 'input', requiring an argument")
    void testIsoInputOption() {
        Option option = CommandLineOptionsManager.ISOINPUT.getOption();

        assertEquals("i", option.getOpt());
        assertEquals("input", option.getLongOpt());
        assertTrue(option.hasArg());
        assertEquals("xmllayout", option.getArgName());
    }

    @Test
    @DisplayName("Should define the ISOOUTPUT option with short 'o' and long 'output', requiring an argument")
    void testIsoOutputOption() {
        Option option = CommandLineOptionsManager.ISOOUTPUT.getOption();

        assertEquals("o", option.getOpt());
        assertEquals("output", option.getLongOpt());
        assertTrue(option.hasArg());
        assertEquals("isoFile", option.getArgName());
    }

    @Test
    @DisplayName("Should declare exactly six command line options")
    void testAllOptionsDeclared() {
        assertEquals(6, CommandLineOptionsManager.values().length);
    }
}
