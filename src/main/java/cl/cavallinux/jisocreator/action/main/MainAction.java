package cl.cavallinux.jisocreator.action.main;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.instances.ActionsManager;
import cl.cavallinux.jisocreator.instances.CommandLineOptionsManager;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.util.JISOCreatorCommandLineParser;
import lombok.Getter;
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
@Getter
public class MainAction extends Action {
    private JISOCreatorCommandLineParser parser;
    
    public MainAction() {
        parser = buildParser();
    }
    
    @Override
    public void run() {
        log.info("Executing app in GUI mode");
        GUIManager.INSTANCE.getMainWindow().setBlockOnOpen(true);
        GUIManager.INSTANCE.getMainWindow().open();
    }

    private void run(String layoutFilePath) {
        GUIManager.INSTANCE.getMainWindow().setBlockOnOpen(true);
        GUIManager.INSTANCE.getMainWindow().open(layoutFilePath);
    }

    /**
     * Metodo principal por donde arranca la app
     * 
     * @param args Argumentos recibidos desde el sistema operativo.
     */
    public static void main(String[] args) throws IOException {
        MainAction mainAction = (MainAction) ActionsManager.MAINACTION.getAction();
        try {
            mainAction.handleCommandLine(args);
        } catch (ParseException e) {
            log.error("Error parsing arguments", e);
            mainAction.getParser().printHelp("jisocreator");
            System.exit(1);
        }
    }

    public void handleCommandLine(String[] args) throws ParseException, IOException {
        CommandLine cmd = parser.parse(args);
        if (cmd.hasOption(CommandLineOptionsManager.LOAD.getOption())) {
            parser.validateLoadXMLFile(cmd, CommandLineOptionsManager.LOAD.getOption());
            run(cmd.getOptionValue(CommandLineOptionsManager.LOAD.getOption()));
            System.exit(0);
        } else if (cmd.hasOption(CommandLineOptionsManager.VERSION.getOption())) {
            parser.printVersion();
            System.exit(0);
        } else if (cmd.hasOption(CommandLineOptionsManager.HELP.getOption())) {
            parser.printHelp("jisocreator");
            System.exit(0);
        } else if (parser.handleCommandLineMode(cmd)) {
            SaveAsIsoAction saveAsIsoAction = (SaveAsIsoAction) ActionsManager.SAVEASISOACTION.getAction();
            saveAsIsoAction.setOutputISOFile(cmd.getOptionValue(CommandLineOptionsManager.ISOOUTPUT.getOption()));
            saveAsIsoAction.setInputXMLLayoutFile(cmd.getOptionValue(CommandLineOptionsManager.ISOINPUT.getOption()));
            saveAsIsoAction.setCommandLineMode(true);
            saveAsIsoAction.run();
        } else {
            run();
        }
    }

    public JISOCreatorCommandLineParser buildParser() {
        String APP_NAME = MainAction.class.getPackage().getSpecificationTitle();
        String APP_VERSION = MainAction.class.getPackage().getImplementationVersion();
        String PROGRAM_NAME = "jisocreator";
        JISOCreatorCommandLineParser parser = JISOCreatorCommandLineParser.builder().applicationName(APP_NAME)
                .applicationVersion(APP_VERSION)
                .header("  " + APP_NAME + " is a Java-based desktop application that simplifies\n "
                        + "the process of creating and editing ISO images. It features dual file\n explorers "
                        + "for managing both the operating system file system and ISO\n" + " image contents.\n")
                .footer("\nExamples:\n" + "  " + PROGRAM_NAME + " -v\n" + "  " + PROGRAM_NAME + " -h\n" + "  "
                        + PROGRAM_NAME + " -i /path/image -o image.iso\n" + "  " + PROGRAM_NAME
                        + " --input /data --output /output/image.iso\n" + "  " + PROGRAM_NAME + " ")
                .options(new Options()).build();
        parser.addOptions();
        return parser;
    }
}