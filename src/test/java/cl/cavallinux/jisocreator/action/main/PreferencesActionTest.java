package cl.cavallinux.jisocreator.action.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jface.resource.ImageDescriptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;
import cl.cavallinux.jisocreator.instances.PreferencesNodeManager;

/**
 * Tests para {@link PreferencesAction}.
 *
 * <p>{@code run()} abre un {@code PreferenceDialog} real usando
 * {@code GUIManager.INSTANCE.getMainWindow().getShell()}, por lo que no es
 * invocable de forma headless.
 *
 * <p><b>{@code createPreferenceManager()} tampoco es headless-testable:</b>
 * internamente invoca {@link PreferencesNodeManager#values()}, cuyo
 * inicializador estatico referencia {@code ImageRegister.INSTANCE}, que a su
 * vez construye {@code ImageUtils} y ejecuta {@code Display.getDefault()}.
 * Esto fuerza la carga de las bibliotecas nativas de SWT del sistema. En el
 * profile Maven por defecto ({@code linux}) esto puede parecer inofensivo si
 * el entorno posee un servidor X activo, pero deja de ser un test unitario
 * aislado: al activar otros profiles de plataforma (p.e. {@code -Pwindows})
 * sobre un host Unix, las bibliotecas nativas resultantes son incompatibles
 * con el sistema operativo real y la JVM del fork de test colapsa de forma
 * irrecuperable ("Libraries for platform win32 cannot be loaded because of
 * incompatible environment"). Por esta razon, y siguiendo la misma
 * convencion aplicada al resto de acciones acopladas a GUI real (ver
 * {@code AboutActionTest}, {@code ExitApplicationActionTest}, etc.), no se
 * ejercita {@code createPreferenceManager()} en este test unitario; solo se
 * cubre el estado construido via builder, que es puro y no toca SWT.
 */
@DisplayName("PreferencesAction tests")
class PreferencesActionTest {

    @Test
    @DisplayName("Builder should set message, tooltip and imageDescriptor")
    void builderShouldSetMessageTooltipAndImageDescriptor() {
        ImageDescriptor descriptor = ImageDescriptor.getMissingImageDescriptor();

        PreferencesAction action = PreferencesAction.builder().message("Preferences").tooltip("Preferences tooltip")
                .imageDescriptor(descriptor).build();

        assertEquals("Preferences", action.getText());
        assertEquals("Preferences tooltip", action.getToolTipText());
        assertSame(descriptor, action.getImageDescriptor());
    }

    @Test
    @DisplayName("Should be an instance of JISOCreatorBaseAction")
    void shouldBeInstanceOfJISOCreatorBaseAction() {
        PreferencesAction action = PreferencesAction.builder().build();

        assertTrue(action instanceof JISOCreatorBaseAction);
    }
}
