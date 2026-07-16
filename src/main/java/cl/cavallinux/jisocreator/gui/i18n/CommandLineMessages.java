package cl.cavallinux.jisocreator.gui.i18n;

import org.eclipse.osgi.util.NLS;

public class CommandLineMessages extends NLS implements INLSBundleMessages {
    public static String commandLineVersionMessage;
    static {
        initializeMessages(COMMANDLINE_BUNDLE_MESSAGE, CommandLineMessages.class);
    }
}
