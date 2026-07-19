package cl.cavallinux.jisocreator.gui.preference;

import java.lang.reflect.Method;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * Extended ComboFieldEditor with improved styling for focus and disabled states.
 * Provides enhanced visual feedback with better contrast in dark themes.
 */
public class StyledComboFieldEditor extends ComboFieldEditor {
    private Color focusBackgroundColor;
    private Color disabledBackgroundColor;
    private Color disabledForegroundColor;

    public StyledComboFieldEditor(String name, String labelText, String[][] entryNamesAndValues, Composite parent) {
        super(name, labelText, entryNamesAndValues, parent);
        initializeStyledColors(parent);
        applyStylesToComboControl();
    }

    private void initializeStyledColors(Composite parent) {
        // Focus state: lighter background for better visibility
        focusBackgroundColor = new Color(parent.getDisplay(), 55, 55, 60);
        // Disabled state: darker background with reduced opacity effect
        disabledBackgroundColor = new Color(parent.getDisplay(), 35, 35, 35);
        // Disabled text: dimmed foreground for visual indication
        disabledForegroundColor = new Color(parent.getDisplay(), 120, 120, 120);
    }

    private void applyStylesToComboControl() {
        Combo comboControl = getComboBoxControlViaReflection();
        if (comboControl != null && !comboControl.isDisposed()) {
            // Add focus listeners for enhanced visual feedback
            comboControl.addListener(SWT.FocusIn, event -> {
                if (!comboControl.isDisposed() && comboControl.getEnabled()) {
                    comboControl.setBackground(focusBackgroundColor);
                }
            });

            comboControl.addListener(SWT.FocusOut, event -> {
                if (!comboControl.isDisposed() && comboControl.getEnabled()) {
                    // Restore to normal input background
                    comboControl.setBackground(new Color(comboControl.getDisplay(), 45, 45, 48));
                }
            });

            // Add state change listener for disabled state
            comboControl.addListener(SWT.Modify, event -> {
                updateComboControlState(comboControl);
            });

            // Also listen for Selection events to catch dropdown selections
            comboControl.addListener(SWT.Selection, event -> {
                updateComboControlState(comboControl);
            });

            // Initial state setup
            updateComboControlState(comboControl);
        }
    }

    private Combo getComboBoxControlViaReflection() {
        try {
            Method method = ComboFieldEditor.class.getDeclaredMethod("getComboBoxControl");
            method.setAccessible(true);
            return (Combo) method.invoke(this);
        } catch (Exception e) {
            // Fallback - try without any arguments
            return null;
        }
    }

    private void updateComboControlState(Combo comboControl) {
        if (comboControl.isDisposed()) {
            return;
        }

        if (!comboControl.getEnabled()) {
            comboControl.setBackground(disabledBackgroundColor);
            comboControl.setForeground(disabledForegroundColor);
        } else if (comboControl.isFocusControl()) {
            comboControl.setBackground(focusBackgroundColor);
            comboControl.setForeground(new Color(comboControl.getDisplay(), 240, 240, 240));
        } else {
            comboControl.setBackground(new Color(comboControl.getDisplay(), 45, 45, 48));
            comboControl.setForeground(new Color(comboControl.getDisplay(), 240, 240, 240));
        }
    }

    @Override
    public void setEnabled(boolean enabled, Composite parent) {
        super.setEnabled(enabled, parent);
        Combo comboControl = getComboBoxControlViaReflection();
        if (comboControl != null && !comboControl.isDisposed()) {
            updateComboControlState(comboControl);
        }
    }

    @Override
    protected void doLoad() {
        super.doLoad();
        applyStylesToComboControl();
    }

    @Override
    protected void doLoadDefault() {
        super.doLoadDefault();
        applyStylesToComboControl();
    }
}
