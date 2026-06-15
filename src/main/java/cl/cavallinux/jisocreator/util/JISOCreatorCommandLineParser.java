package cl.cavallinux.jisocreator.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import cl.cavallinux.jisocreator.instances.CommandLineOptionsManager;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase interna que encapsula el parser configurado.
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
public class JISOCreatorCommandLineParser {
    private final Options options;
    private final String applicationName;
    private final String applicationVersion;
    private final String header;
    private final String footer;

    public CommandLine parse(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    public void addOptions(CommandLineOptionsManager[] optionsToAdd) {
        options.addOption(CommandLineOptionsManager.LOAD.getOption());
        options.addOption(CommandLineOptionsManager.HELP.getOption());
        options.addOption(CommandLineOptionsManager.VERSION.getOption());
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.addOption(CommandLineOptionsManager.ISOINPUT.getOption());
        optionGroup.addOption(CommandLineOptionsManager.ISOOUTPUT.getOption());
        options.addOptionGroup(optionGroup);
    }

    /**
     * Muestra la versión de la aplicación.
     */
    public void printVersion() {
        List<String> versionArguments = new ArrayList<String>();
        versionArguments.add(applicationName);
        versionArguments.add(applicationVersion);
        versionArguments.add(System.getProperty("java.version"));
        versionArguments.add(System.getProperty("java.specification.vendor"));
        versionArguments.add(System.getProperty("os.name"));
        System.out.format("%s version %s\nJVM version: %s\nJVM vendor: %s\n OS host: %s\n", versionArguments.toArray());
    }

    /**
     * Muestra el mensaje de ayuda.
     */
    public void printHelp(String programName) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(programName, header, options, footer, true);
    }

    public void validateLoadXMLFile(CommandLine cmd, Option option) {
        String inputPath = cmd.getOptionValue(option);

        if (StringUtils.isBlank(inputPath)) {
            throw new IllegalArgumentException("Load xml argument required");
        }
        // Validación 2: Validar que la ruta de entrada existe
        File inputDir = new File(inputPath);
        if (!inputDir.exists()) {
            throw new IllegalArgumentException("File not found or doesn't exists: " + inputPath);
        }

        // Validación 3: Validar que la ruta de entrada es accesible
        if (!inputDir.canRead()) {
            throw new IllegalArgumentException("Read and write permissions denied: " + inputPath);
        }

        // Validación 6: Validar extensión del archivo de salida
        if (!inputPath.toLowerCase().endsWith(".xml")) {
            throw new IllegalArgumentException("Input layout is not a xml file: " + inputPath);
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
}