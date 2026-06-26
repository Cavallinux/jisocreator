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
    public static String isoExplorerOpenEntryActionName;
    public static String isoExplorerOpenEntryActionTooltip;
    public static String isoExplorerGoToIsoParentActionName;
    public static String isoExplorerGoToIsoParentActionTooltip;
    public static String isoExplorerShowIsoInfoActionName;
    public static String isoExplorerShowIsoInfoActionTooltip;
    public static String isoExplorerDeleteEntryActionName;
    public static String isoExplorerDeleteEntryActionTooltip;

    static {
        initializeMessages(ISOEXPLORER_BUNDLE_MESSAGE, IsoExplorerMessages.class);
    }
}
