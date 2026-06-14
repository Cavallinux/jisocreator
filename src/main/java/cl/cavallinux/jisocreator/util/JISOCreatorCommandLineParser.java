package cl.cavallinux.jisocreator.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

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

    public void addOption(String shortOpt, String longOpt, String description) {
        options.addOption(Option.builder(shortOpt).longOpt(longOpt).desc(description).build());
    }

    public void addOption(String shortOpt, String longOpt, String argName, String description, boolean required) {
        options.addOption(Option.builder(shortOpt).longOpt(longOpt).hasArg().argName(argName).desc(description)
                .required(required).build());
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
        formatter.printHelp(programName + " [OPCIONES]", header, options, footer, true);
    }
    
    /**
     * Maneja el modo línea de comandos con validación completa de argumentos.
     * 
     * @param cmd CommandLine parseado
     * @throws Exception si hay errores de validación
     */
    public boolean handleCommandLineMode(CommandLine cmd) throws IllegalArgumentException {
        String inputPath = cmd.getOptionValue("i");
        String outputFile = cmd.getOptionValue("o");
        boolean debugMode = cmd.hasOption("d");

        // Validación 1: Verificar que ambas opciones estén presentes si se usa modo CLI
        if ((inputPath == null && outputFile != null) || (inputPath != null && outputFile == null)) {
            throw new IllegalArgumentException(
                    "Se requieren tanto -i (entrada) como -o (salida) cuando se usa modo línea de comandos");
        }

        // Si no hay entrada/salida, terminamos
        if (inputPath == null && outputFile == null) {
            return false;
        }

        // Validación 2: Validar que la ruta de entrada existe
        File inputDir = new File(inputPath);
        if (!inputDir.exists()) {
            throw new IllegalArgumentException("El archivo o directorio de entrada no existe: " + inputPath);
        }

        // Validación 3: Validar que la ruta de entrada es accesible
        if (!inputDir.canRead()) {
            throw new IllegalArgumentException(
                    "No hay permisos para leer el archivo o directorio de entrada: " + inputPath);
        }

        // Validación 4: Validar que la ruta de salida es válida
        File outputFileObj = new File(outputFile);
        File outputDir = outputFileObj.getParentFile();

        if (outputDir != null && !outputDir.exists()) {
            throw new IllegalArgumentException("El directorio de salida no existe: " + outputDir.getAbsolutePath());
        }

        if (outputDir != null && !outputDir.canWrite()) {
            throw new IllegalArgumentException(
                    "No hay permisos de escritura en el directorio de salida: " + outputDir.getAbsolutePath());
        }

        // Validación 5: Advertencia si el archivo de salida ya existe
        if (outputFileObj.exists()) {
            System.out.println("[ADVERTENCIA] El archivo de salida ya existe y será sobrescrito: " + outputFile);
        }

        // Validación 6: Validar extensión del archivo de salida
        if (!outputFile.toLowerCase().endsWith(".iso")) {
            System.out.println("[ADVERTENCIA] Se recomienda que el archivo de salida tenga extensión .iso");
        }

        // Información del procesamiento
        System.out.println("[INFO] ========== Configuración ==========");
        System.out.println("[INFO] Entrada: " + inputPath + (inputDir.isDirectory() ? " (Directorio)" : " (Archivo)"));
        System.out.println("[INFO] Salida: " + outputFile);
        System.out.println("[INFO] Debug: " + (debugMode ? "ACTIVADO" : "DESACTIVADO"));
        System.out.println("[INFO] ====================================");


        System.out.println("[INFO] Modo CLI listo para procesamiento");
        System.out.println("[TODO] Implementar lógica de creación de ISO");
        
        return true;
    }
}