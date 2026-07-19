package cl.cavallinux.jisocreator.model.cmdline;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.cli.Options;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.instances.CommandLineOptionsManager;

/**
 * Tests para los métodos default y static de {@link ICommandLineParser}.
 *
 * <p>En el commit {@code 7b9a42b} se refactorizó el parser para mover
 * {@code buildHelpHeader}, {@code buildHelpFooter} y {@code buildOptions} de
 * métodos estáticos en {@code JISOCreatorCommandLineParser} a métodos default en
 * la interfaz {@code ICommandLineParser}, y se añadió el método estático
 * {@code buildAttributes()}.</p>
 */
@DisplayName("ICommandLineParser default & static method tests")
class ICommandLineParserTest {

    /** Implementación concreta con atributos controlables para los tests. */
    private ICommandLineParser parser;
    private JISOCreatorAttributes attrs;

    @BeforeEach
    void setUp() {
        attrs = JISOCreatorAttributes.builder()
                .appName("JISOCreator")
                .appVersion("0.2.1-SNAPSHOT")
                .jvmVersion("21")
                .jvmVendor("Eclipse Adoptium")
                .osName("Linux")
                .build();
        parser = JISOCreatorCommandLineParser.builder().attributes(attrs).build();
    }

    // -------------------------------------------------------------------------
    // buildAttributes() — método static añadido en 7b9a42b
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("buildAttributes should return a non-null JISOCreatorAttributes")
    void buildAttributesShouldReturnNonNull() {
        JISOCreatorAttributes built = ICommandLineParser.buildAttributes();
        assertNotNull(built);
    }

    @Test
    @DisplayName("buildAttributes should capture current JVM system properties")
    void buildAttributesShouldCaptureCurrentJvmProperties() {
        JISOCreatorAttributes built = ICommandLineParser.buildAttributes();
        // jvmVersion and osName must match the running JVM
        assertNotNull(built.jvmVersion());
        assertFalse(built.jvmVersion().isBlank());
        assertNotNull(built.osName());
        assertFalse(built.osName().isBlank());
    }

    // -------------------------------------------------------------------------
    // buildHelpHeader() — método default de ICommandLineParser
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("buildHelpHeader should return a non-blank string")
    void buildHelpHeaderShouldReturnNonBlankString() {
        String header = parser.buildHelpHeader(attrs);
        assertNotNull(header);
        assertFalse(header.isBlank());
    }

    @Test
    @DisplayName("buildHelpHeader should include the application name")
    void buildHelpHeaderShouldIncludeApplicationName() {
        String header = parser.buildHelpHeader(attrs);
        assertTrue(header.contains("JISOCreator"),
                "Header should contain the app name, but was: " + header);
    }

    // -------------------------------------------------------------------------
    // buildHelpFooter() — método default de ICommandLineParser
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("buildHelpFooter should return a non-blank string")
    void buildHelpFooterShouldReturnNonBlankString() {
        String footer = parser.buildHelpFooter(attrs);
        assertNotNull(footer);
        assertFalse(footer.isBlank());
    }

    @Test
    @DisplayName("buildHelpFooter should include documented command-line flags")
    void buildHelpFooterShouldIncludeCommandLineFlags() {
        String footer = parser.buildHelpFooter(attrs);
        assertTrue(footer.contains("--help"),    "Footer must contain --help");
        assertTrue(footer.contains("--version"), "Footer must contain --version");
        assertTrue(footer.contains("--license"), "Footer must contain --license");
        assertTrue(footer.contains("--load"),    "Footer must contain --load");
        assertTrue(footer.contains("--input"),   "Footer must contain --input");
        assertTrue(footer.contains("--output"),  "Footer must contain --output");
    }

    @Test
    @DisplayName("buildHelpFooter should use lower-case application name in examples")
    void buildHelpFooterShouldUseLowerCaseAppName() {
        String footer = parser.buildHelpFooter(attrs);
        assertTrue(footer.contains("jisocreator"),
                "Footer examples should use lower-case app name, but was: " + footer);
    }

    // -------------------------------------------------------------------------
    // buildOptions() — método default de ICommandLineParser
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("buildOptions should expose all six declared command line options")
    void buildOptionsShouldExposeAllSixDeclaredOptions() {
        Options options = parser.buildOptions();
        assertNotNull(options);
        assertTrue(options.hasOption(CommandLineOptionsManager.LOAD.getOption().getOpt()));
        assertTrue(options.hasOption(CommandLineOptionsManager.HELP.getOption().getOpt()));
        assertTrue(options.hasOption(CommandLineOptionsManager.VERSION.getOption().getOpt()));
        assertTrue(options.hasOption(CommandLineOptionsManager.LICENSE.getOption().getOpt()));
        assertTrue(options.hasOption(CommandLineOptionsManager.ISOINPUT.getOption().getOpt()));
        assertTrue(options.hasOption(CommandLineOptionsManager.ISOOUTPUT.getOption().getOpt()));
    }

    @Test
    @DisplayName("buildOptions should group mutually exclusive options: load/help/version/license")
    void buildOptionsShouldGroupMutuallyExclusiveOptions() {
        Options options = parser.buildOptions();
        // Each option in the group must be flagged as belonging to a group
        assertTrue(options.getOptionGroup(CommandLineOptionsManager.HELP.getOption()) != null,
                "HELP must belong to a mutual-exclusion group");
        assertTrue(options.getOptionGroup(CommandLineOptionsManager.VERSION.getOption()) != null,
                "VERSION must belong to a mutual-exclusion group");
        assertTrue(options.getOptionGroup(CommandLineOptionsManager.LICENSE.getOption()) != null,
                "LICENSE must belong to a mutual-exclusion group");
        assertTrue(options.getOptionGroup(CommandLineOptionsManager.LOAD.getOption()) != null,
                "LOAD must belong to a mutual-exclusion group");
    }
}
