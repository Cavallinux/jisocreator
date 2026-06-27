package cl.cavallinux.jisocreator.instances;

import java.util.Locale;

import cl.cavallinux.jisocreator.gui.i18n.PreferenceDialogMessages;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JISOCreatorLanguageOptions {
    ENGLISH(PreferenceDialogMessages.preferenceDialogGeneralOptionsLanguageENOption, Locale.ENGLISH),
    SPANISH(PreferenceDialogMessages.preferenceDialogGeneralOptionsLanguageESOption, Locale.of("es"));

    private String languageText;
    private Locale languageLocale;
}
