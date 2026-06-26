package cl.cavallinux.jisocreator.gui.preference;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import cl.cavallinux.jisocreator.gui.i18n.PreferenceDialogMessages;
import cl.cavallinux.jisocreator.instances.JISOCreatorLanguageOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneralPreferencesPage extends FieldEditorPreferencePage {
    public static final String NODE_NAME = "preference.general";

    public GeneralPreferencesPage() {
        super(GRID);
        setDescription(PreferenceDialogMessages.preferenceDialogGeneralOptionsNodeDescription);
    }

    @Override
    protected void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        GridLayout layout = (GridLayout) parent.getLayout();
        layout.numColumns = 2;
        addField(new BooleanFieldEditor("general.exit.confirm",
                PreferenceDialogMessages.preferenceDialogGeneralOptionsConfirmExitOption, parent));
        addField(new ComboFieldEditor("jisocreator.language",
                PreferenceDialogMessages.preferenceDialogGeneralOptionsLanguageApp, buildLanguageOptionsArray(),
                parent));
    }

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        getDefaultsButton().setText(PreferenceDialogMessages.preferenceDialogGeneralOptionsRestoreButton);
        getApplyButton().setText(PreferenceDialogMessages.preferenceDialogGeneralOptionsApplyButton);
    }

    private String[][] buildLanguageOptionsArray() {
        Map<String, String> languageOptionsMap = new LinkedHashMap<>();
        Arrays.asList(JISOCreatorLanguageOptions.values()).forEach(language -> {
            languageOptionsMap.put(language.getLanguageText(), language.getLanguageLocale().toLanguageTag());
        });
        return languageOptionsMap.entrySet().stream().map(entry -> new String[] { entry.getKey(), entry.getValue() })
                .toArray(String[][]::new);
    }
}