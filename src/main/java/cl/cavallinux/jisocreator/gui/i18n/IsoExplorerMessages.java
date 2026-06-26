package cl.cavallinux.jisocreator.gui.i18n;

import org.eclipse.osgi.util.NLS;

public class IsoExplorerMessages extends NLS implements INLSBundleMessages {
    public static String isoExplorerSashFormTitle;
    public static String tableColumnsFileNameText;
    public static String tableColumnsFileNameTooltip;
    public static String tableColumnsFileTypeText;
    public static String tableColumnsFileTypeTooltip;
    public static String tableColumnsFileSizeText;
    public static String tableColumnsFileSizeTooltip;
    public static String tableColumnsFileModifiedDateText;
    public static String tableColumnsFileModifiedDateTooltip;
    
    static {
        initializeMessages(ISOEXPLORER_BUNDLE_MESSAGE, IsoExplorerMessages.class);
    }
}
