package cl.cavallinux.jisocreator.gui.preference;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import cl.cavallinux.jisocreator.gui.i18n.PreferenceDialogMessages;
import cl.cavallinux.jisocreator.gui.theme.DarkThemeSupport;
import cl.cavallinux.jisocreator.instances.JISOCreatorLanguageOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneralPreferencesPage extends FieldEditorPreferencePage {
    public static final String NODE_NAME = "preference.general";

    public GeneralPreferencesPage() {
        super(GRID);
        log.info("Initializing GeneralPreferencesPage");
        setDescription(PreferenceDialogMessages.preferenceDialogGeneralOptionsNodeDescription);
    }

    @Override
    protected void createFieldEditors() {
        log.info("Creating field editors for GeneralPreferencesPage");
        Composite parent = getFieldEditorParent();
        GridLayout layout = (GridLayout) parent.getLayout();
        layout.numColumns = 2;
        addField(new BooleanFieldEditor("general.exit.confirm",
                PreferenceDialogMessages.preferenceDialogGeneralOptionsConfirmExitOption, parent));
        addField(new StyledComboFieldEditor("jisocreator.language",
                PreferenceDialogMessages.preferenceDialogGeneralOptionsLanguageApp, buildLanguageOptionsArray(),
                parent));
    }

    @Override
    public void createControl(Composite parent) {
        log.info("Creating control for GeneralPreferencesPage");
        super.createControl(parent);
        getDefaultsButton().setText(PreferenceDialogMessages.preferenceDialogGeneralOptionsRestoreButton);
        getApplyButton().setText(PreferenceDialogMessages.preferenceDialogGeneralOptionsApplyButton);
        DarkThemeSupport.applyToControlTree(getControl());
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