package cl.cavallinux.jisocreator.gui.preference;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class MKISOFSPreferencePage extends FieldEditorPreferencePage {

    public MKISOFSPreferencePage() {
        super(GRID);
        setDescription("MKISOFS parameters configuration");
    }
    
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        getDefaultsButton().setText("Restore");
        getApplyButton().setText("Apply");
    }

    @Override
    protected void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        GridLayout layout = (GridLayout) parent.getLayout();
        layout.numColumns = 2;
        addField(new FileFieldEditor("mkisofs.path", "MKISOFS Path", true, parent));
        addField(new BooleanFieldEditor("mkisofs.rockridge.use", "Use Rock Ridge extension", parent));
        addField(new BooleanFieldEditor("mkisofs.joliet.use", "Use Joliet extension", parent));
        addField(new BooleanFieldEditor("mkisofs.symlinks.follow", "Follow symlinks", parent));
        addField(new ComboFieldEditor("mkisofs.iso.level", "ISO Level:", buildIsoLevelOptionsArray(), parent));
    }

    private String[][] buildIsoLevelOptionsArray() {
        Map<String, String> isoLevelOptionsMap = new LinkedHashMap<>(4);
        isoLevelOptionsMap.put("Level 1 (8.3 names, files max 4GB", "1");
        isoLevelOptionsMap.put("Level 2 (Large names, files max 4GB", "2");
        isoLevelOptionsMap.put("Level 3 (Large names, files > 4GB", "3");
        isoLevelOptionsMap.put("Level 4 (Large names, files > 4GB", "4");

        return isoLevelOptionsMap.entrySet().stream().map(e -> new String[] { e.getKey(), e.getValue() })
                .toArray(String[][]::new);
    }
}