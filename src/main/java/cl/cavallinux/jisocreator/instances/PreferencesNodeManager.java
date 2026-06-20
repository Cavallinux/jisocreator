package cl.cavallinux.jisocreator.instances;

import org.eclipse.jface.preference.PreferenceNode;

import cl.cavallinux.jisocreator.gui.preference.GeneralPreferencesPage;
import cl.cavallinux.jisocreator.gui.preference.MKISOFSPreferencePage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PreferencesNodeManager {
    MKISOFS_PREFERENCE_NODE(new PreferenceNode("preferences.mkisofs", "ISO options",
            ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("iso.png"),
            MKISOFSPreferencePage.class.getName())),
    GENERAL_PREFERENCE_NODE(new PreferenceNode("preferences.general", "General options",
            ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("iso.png"),
            GeneralPreferencesPage.class.getName()));

    private PreferenceNode preferenceNode;
}
