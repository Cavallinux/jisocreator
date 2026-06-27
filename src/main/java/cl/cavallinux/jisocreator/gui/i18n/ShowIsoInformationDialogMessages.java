package cl.cavallinux.jisocreator.gui.i18n;

import org.eclipse.osgi.util.NLS;

public class ShowIsoInformationDialogMessages extends NLS implements INLSBundleMessages {
    public static String showIsoInfoDialogWindowTitle;
    public static String showIsoInfoDialogStaticInfo;
    public static String showIsoInfoDialogVolumeID;
    public static String showIsoInfoDialogApplicationID;
    public static String showIsoInfoDialogPublisherID;
    public static String showIsoInfoDialogIsoSize;
    public static String showIsoInfoDialogIncompleteVolumeIDMessage;
    public static String showIsoInfoDialogVolumeIDGreaterThanMaxMessage;
    
    static {
        initializeMessages(SHOWISOINFO_BUNDLE_MESSAGE, ShowIsoInformationDialogMessages.class);
    }
}
