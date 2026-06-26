package cl.cavallinux.jisocreator.gui.i18n;

import org.eclipse.osgi.util.NLS;

public class MainActionsMessages extends NLS implements INLSBundleMessages {
    public static String newIsoLayoutActionName;
    public static String newIsoLayoutActionTooltip;
    public static String openIsoLayoutActionName;
    public static String openIsoLayoutActionTooltip;
    public static String aboutActionName;
    public static String aboutActionTooltip;
    public static String exitActionName;
    public static String exitActionTooltip;
    public static String preferencesActionName;
    public static String preferencesActionTooltip;
    public static String saveAsIsoActionName;
    public static String saveAsIsoActionTooltip;
    public static String saveAsIsoActionFileNameText;
    public static String saveAsIsoActionFileDialogTitle;
    public static String saveAsXMLActionName;
    public static String saveAsXMLActionTooltip;
    public static String saveAsXMLActionFileNameText;
    public static String saveAsXMLActionFileDialogTitle;
        
    static {
        initializeMessages(MAIN_ACTIONS_BUNDLE_MESSAGE, MainActionsMessages.class);
    }
}
