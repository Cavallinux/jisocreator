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

import cl.cavallinux.jisocreator.action.main.MainAction;
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
    private final Options options = buildOptions();
    @Builder.Default
    private final String header = buildHelpHeader();
    @Builder.Default
    private final String footer = buildHelpFooter();
    @Builder.Default
    private final CommandLineParser commandLineParser = DefaultParser.builder().get();

    public CommandLine parse(String... args) throws ParseException {
        return commandLineParser.parse(options, args);
    }

    @Override
    public void printVersion() {
        List<String> versionArguments = new ArrayList<String>();
        versionArguments.add(MainAction.class.getPackage().getSpecificationTitle());
        versionArguments.add(MainAction.class.getPackage().getImplementationVersion());
        versionArguments.add(System.getProperty("java.version"));
        versionArguments.add(System.getProperty("java.specification.vendor"));
        versionArguments.add(System.getProperty("os.name"));
        System.out.format("%s version %s\nJVM version: %s\nJVM vendor: %s\n OS host: %s\n", versionArguments.toArray());
    }

    @Override
    public void printHelp(String programName) throws IOException {
        HelpFormatter formatter = HelpFormatter.builder().setShowSince(false).get();
        formatter.printHelp(programName, header, options, footer, true);
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
        if (!outputFileObj.exists() || !outputFileObj.canWrite()) {
            throw new ParseException("Output directory doesn't exists or Read and write permissions denied: "
                    + outputFileObj.getAbsolutePath());
        }
    }

    protected static String buildHelpHeader() {
        StringBuilder builder = new StringBuilder();
        builder.append("  ");
        builder.append(JISOCreatorCommandLineParser.class.getPackage().getSpecificationTitle());
        builder.append(" is a Java-based desktop application that simplifies");
        builder.append("\n he process of creating and editing ISO images. It features dual file explorers");
        builder.append("\n for managing both the operating system file system and ISO image contents.\n");
        return builder.toString();
    }

    protected static String buildHelpFooter() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nExamples:"); 
        builder.append("\njisocreator -v");
        builder.append("\njisocreator --version");
        builder.append("\njisocreator -h");
        builder.append("\njisocreator --help");
        builder.append("\njisocreator -i /path/to/xml -o image.iso");
        builder.append("\njisocreator -l layout.xml");
        builder.append("\njisocreator --load layout.xml");
        builder.append("\njisocreator -L");
        builder.append("\njisocreator --license");
        builder.append("\njisocreator --input /path/to/xml --output image.iso");
        builder.append("\njisocreator");
        return builder.toString();
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