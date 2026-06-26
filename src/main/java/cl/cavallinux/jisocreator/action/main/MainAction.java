package cl.cavallinux.jisocreator.action.main;

import java.io.IOException;
import java.util.Locale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceStore;

import cl.cavallinux.jisocreator.instances.ActionsManager;
import cl.cavallinux.jisocreator.instances.CommandLineOptionsManager;
import cl.cavallinux.jisocreator.instances.CommandLineParserManager;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.model.cmdline.ICommandLineParser;
import cl.cavallinux.jisocreator.util.IOUtils;
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
    private ICommandLineParser parser;

    public MainAction() {
        parser = CommandLineParserManager.INSTANCE.getParser();
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
        configureLanguage();
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
        boolean isSaveToIsoOptions = cmd.hasOption(CommandLineOptionsManager.ISOINPUT.getOption())
                && cmd.hasOption(CommandLineOptionsManager.ISOOUTPUT.getOption());
        if (cmd.hasOption(CommandLineOptionsManager.LOAD.getOption())) {
            run(cmd.getOptionValue(CommandLineOptionsManager.LOAD.getOption()));
            System.exit(0);
        } else if (cmd.hasOption(CommandLineOptionsManager.VERSION.getOption())) {
            parser.printVersion();
            System.exit(0);
        } else if (cmd.hasOption(CommandLineOptionsManager.HELP.getOption())) {
            parser.printHelp("jisocreator");
            System.exit(0);
        } else if (isSaveToIsoOptions) {
            parser.handleCommandLine(cmd);
            SaveAsIsoAction saveAsIsoAction = (SaveAsIsoAction) ActionsManager.SAVEASISOACTION.getAction();
            saveAsIsoAction.setOutputISOFile(cmd.getOptionValue(CommandLineOptionsManager.ISOOUTPUT.getOption()));
            saveAsIsoAction.setInputXMLLayoutFile(cmd.getOptionValue(CommandLineOptionsManager.ISOINPUT.getOption()));
            saveAsIsoAction.setCommandLineMode(true);
            saveAsIsoAction.run();
        } else {
            run();
        }
    }
    
    private static void configureLanguage() {
        IOUtils ioUtils = IOManager.INSTANCE.getIoUtils();
        PreferenceStore preferenceStore = ioUtils.getStore();
        String language = preferenceStore.getString("jisocreator.language");
        Locale locale = Locale.of(language);
        Locale.setDefault(locale);
    }
}