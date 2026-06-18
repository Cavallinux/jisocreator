package cl.cavallinux.jisocreator.gui.preference;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneralPreferencesPage extends FieldEditorPreferencePage {
    public GeneralPreferencesPage() {
        super(GRID);
        setDescription("General JISOCREATOR options");
    }

    @Override
    protected void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        GridLayout layout = (GridLayout) parent.getLayout();
        layout.numColumns = 2;
        addField(new BooleanFieldEditor("general.exit.confirm", "Confirm application shutdown on exit", parent));
        addField(new ComboFieldEditor("jisocreator.language", "Language app", buildLanguageOptionsArray(), parent));
    }

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        getDefaultsButton().setText("Restore");
        getApplyButton().setText("Apply");
    }

    private String[][] buildLanguageOptionsArray() {
        Map<String, String> languageOptionsMap = new LinkedHashMap<>();
        languageOptionsMap.put("English", "en");
        languageOptionsMap.put("Español", "es");

        return languageOptionsMap.entrySet().stream().map(entry -> new String[] { entry.getKey(), entry.getValue() })
                .toArray(String[][]::new);
    }
}