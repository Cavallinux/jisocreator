package cl.cavallinux.jisocreator.gui.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.layout.GridLayout;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneralPreferencesPage extends FieldEditorPreferencePage {

    public GeneralPreferencesPage() {
        super(GRID);
        setDescription("General JISOCREATOR options");
    }

    @Override
    protected void createFieldEditors() {
        GridLayout layout = (GridLayout) getFieldEditorParent().getLayout();
        layout.numColumns = 2;
        addField(new BooleanFieldEditor("general.exit.confirm", "Confirm application shutdown on exit",
                getFieldEditorParent()));
    }
}