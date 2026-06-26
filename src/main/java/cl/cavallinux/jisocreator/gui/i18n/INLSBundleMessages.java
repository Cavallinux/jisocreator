package cl.cavallinux.jisocreator.gui.i18n;

import org.eclipse.osgi.util.NLS;

public interface INLSBundleMessages {
    final String MAIN_WINDOW_BUNDLE_MESSAGE = "cl.cavallinux.jisocreator.util.res.i18n.mainwindow.messages";
    final String MAIN_ACTIONS_BUNDLE_MESSAGE = "cl.cavallinux.jisocreator.util.res.i18n.mainactions.messages";
    final String OSEXPLORER_BUNDLE_MESSAGE = "cl.cavallinux.jisocreator.util.res.i18n.osexplorer.messages";
    final String ISOEXPLORER_BUNDLE_MESSAGE = "cl.cavallinux.jisocreator.util.res.i18n.isoexplorer.messages";
    
    static void initializeBundle(String resourceBundle, Class<?> clazz) {
        NLS.initializeMessages(resourceBundle, clazz);
    }
}
