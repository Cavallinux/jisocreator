package cl.cavallinux.jisocreator.model.cmdline;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cl.cavallinux.jisocreator.instances.CommandLineOptionsManager;

@DisplayName("JISOCreatorCommandLineParser Tests")
class JISOCreatorCommandLineParserTest {

    private JISOCreatorCommandLineParser parser;

    @BeforeEach
    void setUp() {
        parser = JISOCreatorCommandLineParser.builder().build();
    }

    @Test
    @DisplayName("Should build options containing all six declared command line options")
    void testBuildOptions() {
        Options options = JISOCreatorCommandLineParser.buildOptions();

        assertTrue(options.hasOption(CommandLineOptionsManager.LOAD.getOption().getOpt()));
        assertTrue(options.hasOption(CommandLineOptionsManager.HELP.getOption().getOpt()));
        assertTrue(options.hasOption(CommandLineOptionsManager.VERSION.getOption().getOpt()));
        assertTrue(options.hasOption(CommandLineOptionsManager.LICENSE.getOption().getOpt()));
        assertTrue(options.hasOption(CommandLineOptionsManager.ISOINPUT.getOption().getOpt()));
        assertTrue(options.hasOption(CommandLineOptionsManager.ISOOUTPUT.getOption().getOpt()));
    }

    @Test
    @DisplayName("Should build a non-empty help header and footer")
    void testBuildHelpHeaderAndFooter() {
        String header = JISOCreatorCommandLineParser.buildHelpHeader();
        String footer = JISOCreatorCommandLineParser.buildHelpFooter();

        assertFalse(header.isBlank());
        assertTrue(footer.contains("--help"));
        assertTrue(footer.contains("--version"));
        assertTrue(footer.contains("--license"));
        assertTrue(footer.contains("--load"));
        assertTrue(footer.contains("--input"));
        assertTrue(footer.contains("--output"));
    }

    @Test
    @DisplayName("Should parse the short help option (-h)")
    void testParseShortHelpOption() throws ParseException {
        CommandLine cmd = parser.parse("-h");

        assertTrue(cmd.hasOption(CommandLineOptionsManager.HELP.getOption().getOpt()));
    }

    @Test
    @DisplayName("Should parse the long help option (--help)")
    void testParseLongHelpOption() throws ParseException {
        CommandLine cmd = parser.parse("--help");

        assertTrue(cmd.hasOption(CommandLineOptionsManager.HELP.getOption().getOpt()));
    }

    @Test
    @DisplayName("Should parse the short version option (-v)")
    void testParseShortVersionOption() throws ParseException {
        CommandLine cmd = parser.parse("-v");

        assertTrue(cmd.hasOption(CommandLineOptionsManager.VERSION.getOption().getOpt()));
    }

    @Test
    @DisplayName("Should parse the long version option (--version)")
    void testParseLongVersionOption() throws ParseException {
        CommandLine cmd = parser.parse("--version");

        assertTrue(cmd.hasOption(CommandLineOptionsManager.VERSION.getOption().getOpt()));
    }

    @Test
    @DisplayName("Should parse the short license option (-L)")
    void testParseShortLicenseOption() throws ParseException {
        CommandLine cmd = parser.parse("-L");

        assertTrue(cmd.hasOption(CommandLineOptionsManager.LICENSE.getOption().getOpt()));
    }

    @Test
    @DisplayName("Should parse the long license option (--license)")
    void testParseLongLicenseOption() throws ParseException {
        CommandLine cmd = parser.parse("--license");

        assertTrue(cmd.hasOption(CommandLineOptionsManager.LICENSE.getOption().getOpt()));
    }

    @Test
    @DisplayName("Should parse the load option with its argument (-l layout.xml)")
    void testParseLoadOptionWithArgument() throws ParseException {
        CommandLine cmd = parser.parse("-l", "layout.xml");

        assertTrue(cmd.hasOption(CommandLineOptionsManager.LOAD.getOption().getOpt()));
        assertEquals("layout.xml", cmd.getOptionValue(CommandLineOptionsManager.LOAD.getOption().getOpt()));
    }

    @Test
    @DisplayName("Should parse the input and output options together (-i input.xml -o output.iso)")
    void testParseInputAndOutputOptions() throws ParseException {
        CommandLine cmd = parser.parse("-i", "input.xml", "-o", "output.iso");

        assertTrue(cmd.hasOption(CommandLineOptionsManager.ISOINPUT.getOption().getOpt()));
        assertTrue(cmd.hasOption(CommandLineOptionsManager.ISOOUTPUT.getOption().getOpt()));
        assertEquals("input.xml", cmd.getOptionValue(CommandLineOptionsManager.ISOINPUT.getOption().getOpt()));
        assertEquals("output.iso", cmd.getOptionValue(CommandLineOptionsManager.ISOOUTPUT.getOption().getOpt()));
    }

    @Test
    @DisplayName("Should parse successfully when no arguments are supplied")
    void testParseWithNoArguments() throws ParseException {
        CommandLine cmd = parser.parse();

        assertFalse(cmd.hasOption(CommandLineOptionsManager.HELP.getOption().getOpt()));
        assertFalse(cmd.hasOption(CommandLineOptionsManager.VERSION.getOption().getOpt()));
    }

    @Test
    @DisplayName("Should throw ParseException when mutually exclusive options are combined (-h -v)")
    void testParseMutuallyExclusiveOptionsThrows() {
        assertThrows(ParseException.class, () -> parser.parse("-h", "-v"));
    }

    @Test
    @DisplayName("Should throw ParseException when the load option argument is missing")
    void testParseLoadOptionMissingArgumentThrows() {
        assertThrows(ParseException.class, () -> parser.parse("-l"));
    }

    @Test
    @DisplayName("Should throw ParseException for an unrecognized option")
    void testParseUnrecognizedOptionThrows() {
        assertThrows(ParseException.class, () -> parser.parse("--unknown-option"));
    }

    @Test
    @DisplayName("Should print version information without throwing")
    void testPrintVersionDoesNotThrow() {
        assertDoesNotThrow(() -> parser.printVersion());
    }

    @Test
    @DisplayName("Should print help information without throwing")
    void testPrintHelpDoesNotThrow() throws IOException {
        assertDoesNotThrow(() -> parser.printHelp("jisocreator"));
    }

    @Test
    @DisplayName("Should use JISOCreatorCommandLineHelpFormatter as the help formatter")
    void testHelpFormatterIsJISOCreatorCommandLineHelpFormatter() {
        assertTrue(parser.getHelpFormatter() instanceof JISOCreatorCommandLineHelpFormatter);
    }

    @Test
    @DisplayName("Should not throw when handling command line with valid readable input and writable output")
    void testHandleCommandLineWithValidPaths(@TempDir Path tempDir) throws IOException, ParseException {
        File inputFile = tempDir.resolve("input.xml").toFile();
        Files.write(inputFile.toPath(), "<root/>".getBytes());
        File outputFile = tempDir.resolve("output.iso").toFile();
        outputFile.createNewFile();

        CommandLine cmd = parser.parse("-i", inputFile.getAbsolutePath(), "-o", outputFile.getAbsolutePath());

        assertDoesNotThrow(() -> parser.handleCommandLine(cmd));
    }

    @Test
    @DisplayName("Should throw ParseException when the input file does not exist")
    void testHandleCommandLineWithNonExistentInputThrows(@TempDir Path tempDir) throws ParseException {
        File nonExistentInput = tempDir.resolve("missing-input.xml").toFile();
        File outputFile = tempDir.resolve("output.iso").toFile();

        CommandLine cmd = parser.parse("-i", nonExistentInput.getAbsolutePath(), "-o", outputFile.getAbsolutePath());

        assertThrows(ParseException.class, () -> parser.handleCommandLine(cmd));
    }

    @Test
    @DisplayName("Should throw ParseException when the output parent directory does not exist")
    void testHandleCommandLineWithNonExistentOutputThrows(@TempDir Path tempDir) throws IOException, ParseException {
        File inputFile = tempDir.resolve("input.xml").toFile();
        Files.write(inputFile.toPath(), "<root/>".getBytes());
        File nonExistentOutput = tempDir.resolve("nonexistent-subdir").resolve("missing-output.iso").toFile();

        CommandLine cmd = parser.parse("-i", inputFile.getAbsolutePath(), "-o",
                nonExistentOutput.getAbsolutePath());

        assertThrows(ParseException.class, () -> parser.handleCommandLine(cmd));
    }
}
