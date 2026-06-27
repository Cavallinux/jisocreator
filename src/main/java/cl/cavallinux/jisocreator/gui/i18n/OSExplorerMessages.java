package cl.cavallinux.jisocreator.gui.i18n;

import org.eclipse.osgi.util.NLS;

public class OSExplorerMessages extends NLS implements INLSBundleMessages {
    public static String osExplorerSashFormTitle;
    public static String tableColumnsFileNameText;
    public static String tableColumnsFileNameTooltip;
    public static String tableColumnsFileTypeText;
    public static String tableColumnsFileTypeTooltip;
    public static String tableColumnsFileSizeText;
    public static String tableColumnsFileSizeTooltip;
    public static String tableColumnsFileModifiedDateText;
    public static String tableColumnsFileModifiedDateTooltip;
    public static String osExplorerAddActionName;
    public static String osExplorerAddActionToolTip;
    public static String osExplorerOpenActionName;
    public static String osExplorerOpenActionToolTip;
    public static String osExplorerGoToParentActionName;
    public static String osExplorerGoToParentActionToolTip;
    public static String osExplorerRefreshActionName;
    public static String osExplorerRefreshActionToolTip;
    public static String osExplorerShowHiddenActionName;
    public static String osExplorerShowHiddenActionToolTip;

    static {
        initializeMessages(OSEXPLORER_BUNDLE_MESSAGE, OSExplorerMessages.class);
    }
}
