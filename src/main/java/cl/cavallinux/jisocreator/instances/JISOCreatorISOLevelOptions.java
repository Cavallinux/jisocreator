package cl.cavallinux.jisocreator.instances;

import cl.cavallinux.jisocreator.gui.i18n.PreferenceDialogMessages;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JISOCreatorISOLevelOptions {
    ISOLEVEL1(PreferenceDialogMessages.preferenceDialogIsoOptionsNodeIsoLevel1, "1"),
    ISOLEVEL2(PreferenceDialogMessages.preferenceDialogIsoOptionsNodeIsoLevel2, "2"),
    ISOLEVEL3(PreferenceDialogMessages.preferenceDialogIsoOptionsNodeIsoLevel3, "3"),
    ISOLEVEL4(PreferenceDialogMessages.preferenceDialogIsoOptionsNodeIsoLevel4, "4");

    private String isoLevelText;
    private String isoLevelValue;
}
