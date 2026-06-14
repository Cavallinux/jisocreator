package cl.cavallinux.jisocreator.action.main;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.instances.ActionsManager;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.util.JISOCreatorCommandLineParser;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase principal, contiene el metodo main para arrancar la aplicacion.
 * 
 * Soporta dos modos de operación: 1. Modo GUI: Sin argumentos, lanza la
 * interfaz gráfica 2. Modo CLI: Con argumentos -i/-o, procesa desde línea de
 * comandos
 * 
 * @author Paolo Mezzano Barahona (pmezzano@gmail.com)
 * @version 0.1.0
 * @since 0.0.2
 */
@Slf4j
public class MainAction extends Action {
    private static final String APP_NAME = MainAction.class.getPackage().getSpecificationTitle();
    private static final String APP_VERSION = MainAction.class.getPackage().getImplementationVersion();
    private static final String PROGRAM_NAME = "jisocreator";
    
    @Override
    public void run() {
        log.info("Executing app in GUI mode");
        GUIManager.INSTANCE.getMainWindow().setBlockOnOpen(true);
        GUIManager.INSTANCE.getMainWindow().open();
    }

    /**
     * Metodo principal por donde arranca la app
     * 
     * @param args Argumentos recibidos desde el sistema operativo.
     */
    public static void main(String[] args) {
        try {
            JISOCreatorCommandLineParser parser = buildParser();
            CommandLine cmd = parser.parse(args);
            boolean commandLineMode = false;
            MainAction mainAction = (MainAction) ActionsManager.MAINACTION.getAction();

            if (cmd.hasOption("v")) {
                parser.printVersion();
                System.exit(0);
            }

            if (cmd.hasOption("h")) {
                parser.printHelp(PROGRAM_NAME);
                System.exit(0);
            }

            if (cmd.hasOption("i") || cmd.hasOption("o")) {
                commandLineMode = parser.handleCommandLineMode(cmd);
            }
            
            if (commandLineMode) {
                SaveAsIsoAction saveAsIsoAction = (SaveAsIsoAction) ActionsManager.SAVEASISOACTION.getAction();
                saveAsIsoAction.setAppArguments(Arrays.asList(args));
                saveAsIsoAction.setCommandLineMode(commandLineMode);
                saveAsIsoAction.run();
            } else {
                mainAction.run();
            }
           
        } catch (ParseException | IllegalArgumentException e) {
            System.err.format("[ERROR] Error parsing app arguments: %s\n" + e.getMessage());
            System.err.format("Use -h or --help to view available options.\n");
            System.exit(1);
        }
    }

    /**
     * Construye el parser utilizando el patrón builder.
     */
    private static JISOCreatorCommandLineParser buildParser() {
        JISOCreatorCommandLineParser parser = JISOCreatorCommandLineParser.builder().applicationName(APP_NAME)
                .applicationVersion(APP_VERSION)
                .header("\n JisoCreator is a Java-based desktop application that simplifies\n "
                        + "the process of creating and editing ISO images. It features dual file\n explorers "
                        + "for managing both the operating system file system and ISO image contents.\n")
                .footer("\nExamples:\n" + "  " + PROGRAM_NAME + " -v\n" + "  " + PROGRAM_NAME + " -h\n" + "  "
                        + PROGRAM_NAME + " -i /path/image -o imagen.iso\n" + "  " + PROGRAM_NAME
                        + " --input /data --output /output/image.iso -d\n"
                        + "\nNo arguments: Open GUI\n")
                .options(new Options()).build();
        // Opciones simples
        parser.addOption("h", "help", "Show this help message");
        parser.addOption("v", "version", "Show application version and jvm version");
        parser.addOption("d", "debug", "Start debug mode");
        // Opciones que requieren argumentos
        parser.addOption("i", "input", "PATH", "Input path", false);
        parser.addOption("o", "output", "FILE", "ISO File output path", false);
        return parser;
    }
}