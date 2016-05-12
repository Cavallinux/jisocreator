package cl.cavallinux.jisocreator.gui.preference;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

public class MKISOFSPreferencePage extends PreferencePage {
    private List<FieldEditor> fieldEditors;

    @Override
    protected Control createContents(Composite parent) {
	setTitle("Iso options");
	Composite top = new Composite(parent, SWT.NONE);
	fieldEditors = new ArrayList<FieldEditor>();
	FieldEditor editor = new FileFieldEditor("mkisofs.path", "MKISOFS Path", true,
		StringButtonFieldEditor.VALIDATE_ON_FOCUS_LOST, top);
	fieldEditors.add(editor);

	Group optionsGroup = new Group(top, SWT.NONE);
	optionsGroup.setText("Iso creations options");

	editor = new BooleanFieldEditor("mkisofs.rockridge.use", "Use RockRidge Extension", optionsGroup);
	fieldEditors.add(editor);

	editor = new BooleanFieldEditor("mkisofs.joliet.use", "Use Joliet Extension", optionsGroup);
	fieldEditors.add(editor);

	editor = new BooleanFieldEditor("mkisofs.symlinks.follow", "Follow symbolic links", optionsGroup);
	fieldEditors.add(editor);

	for (FieldEditor fieldEditor : fieldEditors) {
	    fieldEditor.setPage(this);
	    fieldEditor.setPreferenceStore(getPreferenceStore());
	    fieldEditor.load();
	}

	GridDataFactory.defaultsFor(optionsGroup).grab(true, false).span(3, 1).applyTo(optionsGroup);
	GridLayoutFactory.swtDefaults().generateLayout(optionsGroup);
	GridLayoutFactory.fillDefaults().numColumns(3).generateLayout(top);
	return top;
    }

    @Override
    public boolean performOk() {
	performApply();
	return super.performOk();
    }

    @Override
    protected void performDefaults() {
	for (FieldEditor fieldEditor : fieldEditors) {
	    fieldEditor.loadDefault();
	}
    }

    @Override
    protected void performApply() {
	for (FieldEditor fieldEditor : fieldEditors) {
	    fieldEditor.store();
	}
    }
}