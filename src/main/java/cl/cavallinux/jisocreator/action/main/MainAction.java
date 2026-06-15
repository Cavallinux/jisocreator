package cl.cavallinux.jisocreator.action.main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.instances.ActionsManager;
import cl.cavallinux.jisocreator.instances.CommandLineOptionsManager;
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
    @Override
    public void run() {
        log.info("Executing app in GUI mode");
        GUIManager.INSTANCE.getMainWindow().setBlockOnOpen(true);
        GUIManager.INSTANCE.getMainWindow().open();
    }

    public void run(String layoutFilePath) {
        GUIManager.INSTANCE.getMainWindow().setBlockOnOpen(true);
        GUIManager.INSTANCE.getMainWindow().open(layoutFilePath);
    }

    /**
     * Metodo principal por donde arranca la app
     * 
     * @param args Argumentos recibidos desde el sistema operativo.
     */
    public static void main(String[] args) {
        JISOCreatorCommandLineParser parser = buildParser();
        try {
            CommandLine cmd = parser.parse(args);
            boolean commandLineMode = false;
            MainAction mainAction = (MainAction) ActionsManager.MAINACTION.getAction();
            if (cmd.hasOption(CommandLineOptionsManager.LOAD.getOption())) {
                mainAction.run(cmd.getOptionValue(CommandLineOptionsManager.LOAD.getOption()));
            } else {
                handleCommandLine(args, parser, cmd, commandLineMode, mainAction);
            }
        } catch (ParseException | IllegalArgumentException e) {
            parser.printHelp(MainAction.class.getPackage().getSpecificationTitle());
            System.exit(1);
        }
    }

    private static void handleCommandLine(String[] args, JISOCreatorCommandLineParser parser, CommandLine cmd,
            boolean commandLineMode, MainAction mainAction) {
        if (cmd.hasOption(CommandLineOptionsManager.VERSION.getOption())) {
            parser.printVersion();
            System.exit(0);
        }

        if (cmd.hasOption(CommandLineOptionsManager.HELP.getOption())) {
            parser.printHelp(MainAction.class.getPackage().getSpecificationTitle());
            System.exit(0);
        }

        if (cmd.hasOption(CommandLineOptionsManager.ISOINPUT.getOption())
                || cmd.hasOption(CommandLineOptionsManager.ISOOUTPUT.getOption())) {
            commandLineMode = parser.handleCommandLineMode(cmd);
        }

        if (commandLineMode) {
            SaveAsIsoAction saveAsIsoAction = (SaveAsIsoAction) ActionsManager.SAVEASISOACTION.getAction();
            saveAsIsoAction.setOutputISOFile(cmd.getOptionValue(CommandLineOptionsManager.ISOOUTPUT.getOption()));
            saveAsIsoAction.setInputXMLLayoutFile(cmd.getOptionValue(CommandLineOptionsManager.ISOINPUT.getOption()));
            saveAsIsoAction.setCommandLineMode(commandLineMode);
            saveAsIsoAction.run();
        } else {
            mainAction.run();
        }
    }

    /**
     * Construye el parser utilizando el patrón builder.
     */
    private static JISOCreatorCommandLineParser buildParser() {
        String APP_NAME = MainAction.class.getPackage().getSpecificationTitle();
        String APP_VERSION = MainAction.class.getPackage().getImplementationVersion();
        String PROGRAM_NAME = "jisocreator";
        JISOCreatorCommandLineParser parser = JISOCreatorCommandLineParser.builder().applicationName(APP_NAME)
                .applicationVersion(APP_VERSION)
                .header("\n " + APP_NAME + "is a Java-based desktop application that simplifies\n "
                        + "the process of creating and editing ISO images. It features dual file\n explorers "
                        + "for managing both the operating system file system and ISO\n" + "image contents.\n")
                .footer("\nExamples:\n" + "  " + PROGRAM_NAME + " -v\n" + "  " + PROGRAM_NAME + " -h\n" + "  "
                        + PROGRAM_NAME + " -i /path/image -o image.iso\n" + "  " + PROGRAM_NAME
                        + " --input /data --output /output/image.iso\n" + "  " + PROGRAM_NAME + " ")
                .options(new Options()).build();
        parser.addOptions(CommandLineOptionsManager.values());
        return parser;
    }
}