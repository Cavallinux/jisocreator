package cl.cavallinux.jisocreator.gui.i18n;

import org.eclipse.osgi.util.NLS;

public class MainWindowMessages extends NLS implements INLSBundleMessages {
    public static String fileMenuName;
    public static String toolsMenuName;
    public static String helpMenuName;
    public static String fileSaveAsName;
    public static String windowTitle;;
    
    static {
        initializeMessages(MAIN_WINDOW_BUNDLE_MESSAGE, MainWindowMessages.class);
    }
}
