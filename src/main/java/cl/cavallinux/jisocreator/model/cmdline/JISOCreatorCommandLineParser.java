package cl.cavallinux.jisocreator.model.cmdline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.help.HelpFormatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import cl.cavallinux.jisocreator.gui.i18n.CommandLineMessages;
import cl.cavallinux.jisocreator.instances.CommandLineOptionsManager;
import cl.cavallinux.jisocreator.instances.IOManager;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase interna que encapsula el parser configurado.
 */

@Getter
@Setter
@Builder
public class JISOCreatorCommandLineParser implements ICommandLineParser {
    private static final String APP_NAME = JISOCreatorCommandLineParser.class.getPackage().getSpecificationTitle();
    private static final String APP_VERSION = JISOCreatorCommandLineParser.class.getPackage()
            .getImplementationVersion();
    private static final String JVM_VERSION = System.getProperty("java.version");
    private static final String JVM_VENDOR = System.getProperty("java.specification.vendor");
    private static final String OS_NAME = System.getProperty("os.name");
    private static final String LOWERCASE_APPNAME = StringUtils.toRootLowerCase(APP_NAME);
    @Builder.Default
    private final Options options = buildOptions();
    @Builder.Default
    private final String header = buildHelpHeader();
    @Builder.Default
    private final String footer = buildHelpFooter();
    @Builder.Default
    private final CommandLineParser commandLineParser = DefaultParser.builder().get();
    @Builder.Default
    private final HelpFormatter helpFormatter = new JISOCreatorCommandLineHelpFormatter(
            HelpFormatter.builder().setShowSince(false));

    public CommandLine parse(String... args) throws ParseException {
        return commandLineParser.parse(options, args);
    }

    @Override
    public void printVersion() {
        List<String> versionArguments = new ArrayList<String>();
        versionArguments.add(APP_NAME);
        versionArguments.add(APP_VERSION);
        versionArguments.add(JVM_VERSION);
        versionArguments.add(JVM_VENDOR);
        versionArguments.add(OS_NAME);
        System.out.format(CommandLineMessages.commandLineVersionMessage, versionArguments.toArray());
    }

    @Override
    public void printHelp(String programName) throws IOException {
        helpFormatter.printHelp(programName, header, options, footer, true);
    }

    @Override
    public void handleCommandLine(CommandLine cmd) throws ParseException {
        String inputPath = cmd.getOptionValue(CommandLineOptionsManager.ISOINPUT.getOption());
        String outputFile = cmd.getOptionValue(CommandLineOptionsManager.ISOOUTPUT.getOption());

        File inputDir = new File(inputPath);
        if (!inputDir.exists() || !inputDir.canRead()) {
            throw new ParseException("Input file doesn't exist or read and write permissions denied: ");
        }

        File outputFileObj = new File(outputFile);
        if (!outputFileObj.getParentFile().exists() || !outputFileObj.getParentFile().canWrite()) {
            throw new ParseException("Output directory doesn't exists or Read and write permissions denied: "
                    + outputFileObj.getAbsolutePath());
        }
    }

    protected static String buildHelpHeader() {
        List<String> helpHeaderArguments = new ArrayList<String>();
        helpHeaderArguments.add(APP_NAME);
        return String.format(CommandLineMessages.commandLineAppDescriptionMessage, helpHeaderArguments.toArray());
    }

    protected static String buildHelpFooter() {
        return Strings.CI.replace(CommandLineMessages.commandLineExampleUsageMessage, "%s", LOWERCASE_APPNAME);
    }

    protected static Options buildOptions() {
        Options options = new Options();
        options.addOption(CommandLineOptionsManager.ISOINPUT.getOption());
        options.addOption(CommandLineOptionsManager.ISOOUTPUT.getOption());
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.addOption(CommandLineOptionsManager.LOAD.getOption());
        optionGroup.addOption(CommandLineOptionsManager.HELP.getOption());
        optionGroup.addOption(CommandLineOptionsManager.VERSION.getOption());
        optionGroup.addOption(CommandLineOptionsManager.LICENSE.getOption());
        options.addOptionGroup(optionGroup);
        return options;
    }

    @Override
    public void printLicense() {
        System.out.println(IOManager.INSTANCE.getIoUtils().loadFormattedLicenseFile());
    }
}