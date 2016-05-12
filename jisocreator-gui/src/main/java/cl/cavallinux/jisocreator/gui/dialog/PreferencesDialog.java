package cl.cavallinux.jisocreator.gui.dialog;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.widgets.Shell;

import cl.cavallinux.jisocreator.gui.preference.MKISOFSPreferencePage;
import cl.cavallinux.jisocreator.util.IOUtils;
import cl.cavallinux.jisocreator.util.ImageUtils;

public class PreferencesDialog extends PreferenceDialog {

    public PreferencesDialog(Shell parentShell, PreferenceManager manager) {
	super(parentShell, manager);
	getPreferenceManager().addToRoot(new PreferenceNode("preferences.mkisofs", "ISO options",
		ImageUtils.getInstance().loadImageDescriptor("iso.png"), MKISOFSPreferencePage.class.getName()));
	setPreferenceStore(IOUtils.getInstance().getStore());
    }

    @Override
    protected void configureShell(Shell newShell) {
	super.configureShell(newShell);
	newShell.setText("JIsocreator Preferences");
    }

    @Override
    protected void okPressed() {
	try {
	    ((PreferenceStore) getPreferenceStore()).save();
	} catch (IOException e) {
	    Logger.getAnonymousLogger().log(Level.SEVERE, "I/O Error", e);
	}
	super.okPressed();
    }
}