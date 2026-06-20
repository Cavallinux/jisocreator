package cl.cavallinux.jisocreator.gui.listeners.dialog;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * Adaptador reutilizable para capturar la pulsación de la tecla Enter en
 * componentes de texto de una sola línea de SWT (SWT.SINGLE).
 */
public class EnterKeySubmitAdapter extends SelectionAdapter {

    private final Runnable submitAction;

    /**
     * Constructor que recibe la acción a ejecutar cuando se presione Enter.
     * 
     * @param submitAction Acción/método a ejecutar (normalmente la
     *                     validación/cierre del diálogo).
     */
    public EnterKeySubmitAdapter(Runnable submitAction) {
        this.submitAction = submitAction;
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // widgetDefaultSelected se dispara automáticamente al pulsar Enter.
        if (submitAction != null) {
            submitAction.run();
        }
    }
}