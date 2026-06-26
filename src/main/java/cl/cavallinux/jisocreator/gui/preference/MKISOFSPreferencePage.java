package cl.cavallinux.jisocreator.gui.preference;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import cl.cavallinux.jisocreator.gui.i18n.PreferenceDialogMessages;
import cl.cavallinux.jisocreator.instances.JISOCreatorISOLevelOptions;

public class MKISOFSPreferencePage extends FieldEditorPreferencePage {
    public static final String NODE_NAME = "preferences.general";

    public MKISOFSPreferencePage() {
        super(GRID);
        setDescription(PreferenceDialogMessages.preferenceDialogIsoOptionsNodeDescription);
    }

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        getApplyButton().setText(PreferenceDialogMessages.preferenceDialogIsoOptionsNodeApplyButton);
        getDefaultsButton().setText(PreferenceDialogMessages.preferenceDialogIsoOptionsNodeRestoreButton);
    }

    @Override
    protected void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        GridLayout layout = (GridLayout) parent.getLayout();
        layout.numColumns = 2;
        FileFieldEditor mkisofsPathBrowser = new FileFieldEditor("mkisofs.path",
                PreferenceDialogMessages.preferenceDialogIsoOptionsNodeMKISOFSPath, true, parent);
        mkisofsPathBrowser.setChangeButtonText(
                PreferenceDialogMessages.preferenceDialogIsoOptionsNodeMKISOFSPathBrowseButtonText);
        addField(mkisofsPathBrowser);
        addField(new BooleanFieldEditor("mkisofs.rockridge.use",
                PreferenceDialogMessages.preferenceDialogIsoOptionsNodeRockRidgeExtension, parent));
        addField(new BooleanFieldEditor("mkisofs.joliet.use",
                PreferenceDialogMessages.preferenceDialogIsoOptionsNodeJolietExtension, parent));
        addField(new BooleanFieldEditor("mkisofs.symlinks.follow",
                PreferenceDialogMessages.preferenceDialogIsoOptionsNodeFollowSymlinks, parent));
        addField(new ComboFieldEditor("mkisofs.iso.level",
                PreferenceDialogMessages.preferenceDialogIsoOptionsNodeIsoLevel, buildIsoLevelOptionsArray(), parent));
    }

    private String[][] buildIsoLevelOptionsArray() {
        JISOCreatorISOLevelOptions[] isoLevels = JISOCreatorISOLevelOptions.values();
        Map<String, String> isoLevelOptionsMap = LinkedHashMap.newLinkedHashMap(isoLevels.length);
        Arrays.asList(isoLevels).forEach(isoLevel -> {
            isoLevelOptionsMap.put(isoLevel.getIsoLevelText(), isoLevel.getIsoLevelValue());
        });

        return isoLevelOptionsMap.entrySet().stream().map(e -> new String[] { e.getKey(), e.getValue() })
                .toArray(String[][]::new);
    }
}