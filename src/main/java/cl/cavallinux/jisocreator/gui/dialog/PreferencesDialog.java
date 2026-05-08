package cl.cavallinux.jisocreator.gui.dialog;

import java.io.IOException;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.widgets.Shell;

import cl.cavallinux.jisocreator.gui.preference.MKISOFSPreferencePage;
import cl.cavallinux.jisocreator.util.IOUtils;
import cl.cavallinux.jisocreator.util.ImageUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PreferencesDialog extends PreferenceDialog {
    public PreferencesDialog(Shell parentShell, PreferenceManager manager) {
        super(parentShell, manager);
        log.info("Initializing PreferencesDialog");
        getPreferenceManager().addToRoot(new PreferenceNode("preferences.mkisofs", "ISO options",
                ImageUtils.getInstance().loadImageDescriptor("iso.png"), MKISOFSPreferencePage.class.getName()));
        setPreferenceStore(IOUtils.getInstance().getStore());
    }

    @Override
    protected void configureShell(Shell newShell) {
        log.info("Configuring shell for PreferencesDialog");
        super.configureShell(newShell);
        newShell.setText("JIsocreator Preferences");
    }

    @Override
    protected void okPressed() {
        try {
            log.info("Saving preferences");
            ((PreferenceStore) getPreferenceStore()).save();
            log.info("Preferences saved successfully");
        } catch (IOException e) {
            log.error("Failed to save preferences", e);
        } finally {
            log.info("Closing PreferencesDialog");
            super.okPressed();
        }
    }
}