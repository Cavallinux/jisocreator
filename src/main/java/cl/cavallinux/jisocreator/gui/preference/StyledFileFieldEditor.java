package cl.cavallinux.jisocreator.gui.preference;

import java.lang.reflect.Method;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Extended FileFieldEditor with improved styling for focus and disabled states.
 * Provides enhanced visual feedback with better contrast in dark themes.
 */
public class StyledFileFieldEditor extends FileFieldEditor {
    private Color focusBackgroundColor;
    private Color disabledBackgroundColor;
    private Color disabledForegroundColor;

    public StyledFileFieldEditor(String name, String labelText, boolean enforceAbsolutePath, Composite parent) {
        super(name, labelText, enforceAbsolutePath, parent);
        initializeStyledColors(parent);
        applyStylesToTextControl();
    }

    public StyledFileFieldEditor(String name, String labelText, boolean enforceAbsolutePath, int validationStrategy,
            Composite parent) {
        super(name, labelText, enforceAbsolutePath, validationStrategy, parent);
        initializeStyledColors(parent);
        applyStylesToTextControl();
    }

    private void initializeStyledColors(Composite parent) {
        // Focus state: lighter background for better visibility
        focusBackgroundColor = new Color(parent.getDisplay(), 55, 55, 60);
        // Disabled state: darker background with reduced opacity effect
        disabledBackgroundColor = new Color(parent.getDisplay(), 35, 35, 35);
        // Disabled text: dimmed foreground for visual indication
        disabledForegroundColor = new Color(parent.getDisplay(), 120, 120, 120);
    }

    private void applyStylesToTextControl() {
        Text textControl = getTextControlViaReflection();
        if (textControl != null && !textControl.isDisposed()) {
            // Add focus listeners for enhanced visual feedback
            textControl.addListener(SWT.FocusIn, event -> {
                if (!textControl.isDisposed() && textControl.getEnabled()) {
                    textControl.setBackground(focusBackgroundColor);
                }
            });

            textControl.addListener(SWT.FocusOut, event -> {
                if (!textControl.isDisposed() && textControl.getEnabled()) {
                    // Restore to normal input background
                    textControl.setBackground(new Color(textControl.getDisplay(), 45, 45, 48));
                }
            });

            // Add state change listener for disabled state
            textControl.addListener(SWT.Modify, event -> {
                updateTextControlState(textControl);
            });

            // Initial state setup
            updateTextControlState(textControl);
        }
    }

    private Text getTextControlViaReflection() {
        try {
            Method method = FileFieldEditor.class.getDeclaredMethod("getTextControl");
            method.setAccessible(true);
            return (Text) method.invoke(this);
        } catch (Exception e) {
            // Fallback - try with Composite parameter
            return null;
        }
    }

    private void updateTextControlState(Text textControl) {
        if (textControl.isDisposed()) {
            return;
        }

        if (!textControl.getEnabled()) {
            textControl.setBackground(disabledBackgroundColor);
            textControl.setForeground(disabledForegroundColor);
        } else if (textControl.isFocusControl()) {
            textControl.setBackground(focusBackgroundColor);
            textControl.setForeground(new Color(textControl.getDisplay(), 240, 240, 240));
        } else {
            textControl.setBackground(new Color(textControl.getDisplay(), 45, 45, 48));
            textControl.setForeground(new Color(textControl.getDisplay(), 240, 240, 240));
        }
    }

    @Override
    public void setEnabled(boolean enabled, Composite parent) {
        super.setEnabled(enabled, parent);
        Text textControl = getTextControlViaReflection();
        if (textControl != null && !textControl.isDisposed()) {
            updateTextControlState(textControl);
        }
    }

    @Override
    protected void doLoad() {
        super.doLoad();
        applyStylesToTextControl();
    }

    @Override
    protected void doLoadDefault() {
        super.doLoadDefault();
        applyStylesToTextControl();
    }
}
