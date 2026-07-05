package cl.cavallinux.jisocreator.action.main;

import java.io.IOException;
import java.util.Locale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceStore;

import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.instances.ActionsManager;
import cl.cavallinux.jisocreator.instances.CommandLineOptionsManager;
import cl.cavallinux.jisocreator.instances.CommandLineParserManager;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.model.cmdline.ICommandLineParser;
import cl.cavallinux.jisocreator.util.IOUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
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
@Setter
@Builder
public class MainAction extends Action {
    @Builder.Default
    private ICommandLineParser parser = CommandLineParserManager.INSTANCE.getParser();
    @Builder.Default
    private String layoutFilePath = StringUtils.EMPTY;

    @Override
    public void run() {
        log.info("Executing app in GUI mode");
        MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
        mainWindow.setBlockOnOpen(true);
        int exitCode = StringUtils.isNotBlank(layoutFilePath) ? mainWindow.open(layoutFilePath) : mainWindow.open();
        log.info("Application exited with code: {}", exitCode);
    }

    /**
     * Metodo principal por donde arranca la app
     * 
     * @param args Argumentos recibidos desde el sistema operativo.
     */
    public static void main(String[] args) throws IOException {
        MainAction mainAction = (MainAction) ActionsManager.MAINACTION.getAction();
        mainAction.configureLanguage();
        try {
            mainAction.handleCommandLine(args);
        } catch (ParseException e) {
            log.error("Error parsing arguments", e);
            mainAction.printHelp();
            System.exit(1);
        }
    }

    public void handleCommandLine(String[] args) throws ParseException, IOException {
        CommandLine cmd = parser.parse(args);
        boolean isSaveToIsoOptions = cmd.hasOption(CommandLineOptionsManager.ISOINPUT.getOption())
                && cmd.hasOption(CommandLineOptionsManager.ISOOUTPUT.getOption());
        if (cmd.hasOption(CommandLineOptionsManager.LOAD.getOption())) {
            setLayoutFilePath(cmd.getOptionValue(CommandLineOptionsManager.LOAD.getOption()));
            run();
        } else if (cmd.hasOption(CommandLineOptionsManager.VERSION.getOption())) {
            parser.printVersion();
            System.exit(0);
        } else if (cmd.hasOption(CommandLineOptionsManager.HELP.getOption())) {
            printHelp();
            System.exit(0);
        } else if (cmd.hasOption(CommandLineOptionsManager.LICENSE.getOption())) {
            parser.printLicense();
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

    private void configureLanguage() {
        IOUtils ioUtils = IOManager.INSTANCE.getIoUtils();
        PreferenceStore preferenceStore = ioUtils.getStore();
        String language = preferenceStore.getString("jisocreator.language");
        Locale.setDefault(Locale.of(language));
    }
    
    private void printHelp() throws IOException {
        parser.printHelp("jisocreator");
    }
}