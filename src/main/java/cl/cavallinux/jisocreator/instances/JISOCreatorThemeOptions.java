package cl.cavallinux.jisocreator.instances;

import cl.cavallinux.jisocreator.gui.i18n.PreferenceDialogMessages;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JISOCreatorThemeOptions {
    AUTO(PreferenceDialogMessages.preferenceDialogGeneralOptionsThemeAutoOption, "AUTO"),
    LIGHT(PreferenceDialogMessages.preferenceDialogGeneralOptionsThemeLightOption, "LIGHT"),
    DARK(PreferenceDialogMessages.preferenceDialogGeneralOptionsThemeDarkOption, "DARK");

    private String themeModeText;
    private String themeModeValue;
}
