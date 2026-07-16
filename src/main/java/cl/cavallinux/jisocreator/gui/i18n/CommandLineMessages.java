package cl.cavallinux.jisocreator.gui.i18n;

import org.eclipse.osgi.util.NLS;

public class CommandLineMessages extends NLS implements INLSBundleMessages {
    public static String commandLineVersionMessage;
    public static String commandLineAppDescriptionMessage;
    public static String commandLineExampleUsageMessage;
    public static String commandLineLoadOptionDescriptionMessage;
    public static String commandLineHelpOptionDescriptionMessage;
    public static String commandLineVersionOptionDescriptionMessage;
    public static String commandLineLicenseOptionDescriptionMessage;
    public static String commandLineIsoInputOptionDescriptionMessage;
    public static String commandLineIsoOutputOptionDescriptionMessage;
    public static String commandLineIsoOptionsTableTitleMessage;
    public static String commandLineIsoOptionsTableOptionColumnMessage;
    public static String commandLineIsoOptionsTableDescriptionColumnMessage;
    public static String commandLineIsoOptionsSyntaxHeaderMessage;
    
    static {
        initializeMessages(COMMANDLINE_BUNDLE_MESSAGE, CommandLineMessages.class);
    }
}
