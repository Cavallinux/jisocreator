package cl.cavallinux.jisocreator.gui.i18n;

import org.eclipse.osgi.util.NLS;

public class AboutDialogMessages extends NLS implements INLSBundleMessages {
    public static String aboutDialogWindowTitle;
    public static String aboutDialogProgramName;
    public static String aboutDialogProgramVersion;
    public static String aboutDialogAboutTabText;
    public static String aboutDialogAboutCompositeText;
    public static String aboutDialogLicenseTabText;
    public static String aboutDialogOKButtonText;
    
    static {
        initializeMessages(ABOUTDIALOG_BUNDLE_MESSAGE, AboutDialogMessages.class);
    }
}
