package cl.cavallinux.jisocreator.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.help.HelpFormatter;
import org.apache.commons.lang3.StringUtils;

import cl.cavallinux.jisocreator.action.main.MainAction;
import cl.cavallinux.jisocreator.instances.CommandLineOptionsManager;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Clase interna que encapsula el parser configurado.
 */

@Getter
@Setter
@SuperBuilder
public class JISOCreatorCommandLineParser extends AbstractJISOCreatorCommandLineParser {
    private final Options options;
    private final String header;
    private final String footer;

    public CommandLine parse(String[] args) throws ParseException {
        return commandLineParser.parse(options, args);
    }

    /**
     * Muestra la versión de la aplicación.
     */
    public void printVersion() {
        List<String> versionArguments = new ArrayList<String>();
        versionArguments.add(MainAction.class.getPackage().getSpecificationTitle());
        versionArguments.add(MainAction.class.getPackage().getImplementationVersion());
        versionArguments.add(System.getProperty("java.version"));
        versionArguments.add(System.getProperty("java.specification.vendor"));
        versionArguments.add(System.getProperty("os.name"));
        System.out.format("%s version %s\nJVM version: %s\nJVM vendor: %s\n OS host: %s\n", versionArguments.toArray());
    }

    /**
     * Muestra el mensaje de ayuda.
     */
    public void printHelp(String programName) throws IOException {
        HelpFormatter formatter = HelpFormatter.builder().setShowSince(false).get();
        formatter.printHelp(programName, header, options, footer, true);
    }

    public void validateLoadXMLFile(CommandLine cmd, Option option) throws ParseException {
        String inputPath = cmd.getOptionValue(option);

        if (StringUtils.isBlank(inputPath)) {
            throw new ParseException("Load xml argument required");
        }
        // Validación 2: Validar que la ruta de entrada existe
        File inputDir = new File(inputPath);
        if (!inputDir.exists()) {
            throw new ParseException("File not found or doesn't exists: " + inputPath);
        }

        // Validación 3: Validar que la ruta de entrada es accesible
        if (!inputDir.canRead()) {
            throw new ParseException("Read and write permissions denied: " + inputPath);
        }

        // Validación 6: Validar extensión del archivo de salida
        if (!inputPath.toLowerCase().endsWith(".xml")) {
            throw new ParseException("Input layout is not a xml file: " + inputPath);
        }
    }

    /**
     * Maneja el modo línea de comandos con validación completa de argumentos.
     * 
     * @param cmd CommandLine parseado
     * @throws Exception si hay errores de validación
     */
    public boolean handleCommandLineMode(CommandLine cmd) throws ParseException {
        String inputPath = null;
        String outputFile = null;

        if (cmd.hasOption(CommandLineOptionsManager.ISOINPUT.getOption())
                && cmd.hasOption(CommandLineOptionsManager.ISOOUTPUT.getOption())) {
            inputPath = cmd.getOptionValue(CommandLineOptionsManager.ISOINPUT.getOption());
            outputFile = cmd.getOptionValue(CommandLineOptionsManager.ISOOUTPUT.getOption());
        } else {
            return false;
        }

        if ((inputPath == null && outputFile != null) || (inputPath != null && outputFile == null)) {
            throw new ParseException("Options -i and -o required");
        }

        if (inputPath == null && outputFile == null) {
            return false;
        }

        File inputDir = new File(inputPath);
        if (!inputDir.exists()) {
            throw new ParseException("Input file doesn't exist" + inputPath);
        }

        if (!inputDir.canRead()) {
            throw new ParseException("Read and write permissions denied: " + inputPath);
        }

        File outputFileObj = new File(outputFile);
        File outputDir = outputFileObj.getParentFile();

        if (outputDir != null && !outputDir.exists()) {
            throw new ParseException("Output directory doesn't exists " + outputDir.getAbsolutePath());
        }

        if (outputDir != null && !outputDir.canWrite()) {
            throw new ParseException("Read and write permissions denied: " + outputDir.getAbsolutePath());
        }

        return true;
    }

    public static String buildHelpHeader() {
        StringBuilder builder = new StringBuilder();
        builder.append("  ");
        builder.append(JISOCreatorCommandLineParser.class.getPackage().getSpecificationTitle());
        builder.append(" is a Java-based desktop application that simplifies");
        builder.append("\n he process of creating and editing ISO images. It features dual file explorers");
        builder.append("\n for managing both the operating system file system and ISO image contents.\n");
        return builder.toString();
    }

    public static String buildHelpFooter() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nExamples:\n jisocreator -v");
        builder.append("\njisocreator -h");
        builder.append("\njisocreator -i /path/to/xml -o image.iso");
        builder.append("\njisocreator --input /path/to/xml --output image.iso");
        builder.append("\njisocreator");
        return builder.toString();
    }

    public static Options buildOptions() {
        Options options = new Options();
        options.addOption(CommandLineOptionsManager.ISOINPUT.getOption());
        options.addOption(CommandLineOptionsManager.ISOOUTPUT.getOption());
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.addOption(CommandLineOptionsManager.LOAD.getOption());
        optionGroup.addOption(CommandLineOptionsManager.HELP.getOption());
        optionGroup.addOption(CommandLineOptionsManager.VERSION.getOption());
        options.addOptionGroup(optionGroup);
        return options;
    }
}