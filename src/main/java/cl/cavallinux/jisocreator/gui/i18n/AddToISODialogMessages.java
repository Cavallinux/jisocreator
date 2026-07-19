package cl.cavallinux.jisocreator.gui.i18n;

import org.eclipse.osgi.util.NLS;

public class AddToISODialogMessages extends NLS implements INLSBundleMessages {
    public static String addToIsoDialogWindowTitle;
    public static String addToIsoDialogStaticInfo;
    public static String addToIsoDialogOKButtonLabel;
    public static String addToIsoDialogOKCancelLabel;

    static {
        NLS.initializeMessages(ADDTOISODIALOG_BUNDLE_MESSAGE, AddToISODialogMessages.class);
    }
}
