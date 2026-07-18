package cl.cavallinux.jisocreator.model.cmdline;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.help.HelpFormatter;

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
    @Builder.Default
    private final JISOCreatorAttributes attributes = ICommandLineParser.buildAttributes();
    @Builder.Default
    private final CommandLineParser commandLineParser = DefaultParser.builder().get();
    @Builder.Default
    private final HelpFormatter helpFormatter = new JISOCreatorCommandLineHelpFormatter();

    public CommandLine parse(String... args) throws ParseException {
        Options options = buildOptions();
        return commandLineParser.parse(options, args);
    }

    @Override
    public void printVersion() {
        List<String> versionArguments = List.of(attributes.appName(), attributes.appVersion(), attributes.jvmVersion(),
                attributes.jvmVendor(), attributes.osName());
        System.out.format(CommandLineMessages.commandLineVersionMessage, versionArguments.toArray());
    }

    @Override
    public void printHelp(String programName) throws IOException {
        String header = buildHelpHeader(attributes);
        Options options = buildOptions();
        String footer = buildHelpFooter(attributes);
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
    
    @Override
    public void printLicense() {
        System.out.println(IOManager.INSTANCE.getIoUtils().loadFormattedLicenseFile());
    }
}