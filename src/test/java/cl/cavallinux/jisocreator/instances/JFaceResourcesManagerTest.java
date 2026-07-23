package cl.cavallinux.jisocreator.instances;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.eclipse.swt.SWT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.gui.listeners.isoexplorer.ISODirectoriesMenuListener;
import cl.cavallinux.jisocreator.gui.listeners.isoexplorer.ISOExplorerSashFormDoubleClickListener;
import cl.cavallinux.jisocreator.gui.listeners.isoexplorer.ISOExplorerSashFormSelectionChangedListener;
import cl.cavallinux.jisocreator.gui.listeners.osexplorer.OSDirectoriesMenuListener;
import cl.cavallinux.jisocreator.gui.listeners.osexplorer.OSExplorerSashFormDoubleClickListener;
import cl.cavallinux.jisocreator.gui.listeners.osexplorer.OSExplorerSashFormSelectionChangedListener;
import cl.cavallinux.jisocreator.model.comparators.ITreeNodeDirectoriesFirstComparator;
import cl.cavallinux.jisocreator.model.comparators.OSDirectoriesComparator;
import cl.cavallinux.jisocreator.model.filters.HideHiddenFilesFilter;
import cl.cavallinux.jisocreator.model.filters.ShowOnlyDirectoriesFilter;
import cl.cavallinux.jisocreator.model.filters.isoexplorer.ShowOnlyIsoDirectoriesFilter;
import cl.cavallinux.jisocreator.model.providers.isoexplorer.IsoTableProvider;
import cl.cavallinux.jisocreator.model.providers.isoexplorer.IsoTreeContentProvider;
import cl.cavallinux.jisocreator.model.providers.isoexplorer.IsoTreeLabelProvider;
import cl.cavallinux.jisocreator.model.providers.osexplorer.OSTreeContentProvider;
import cl.cavallinux.jisocreator.model.providers.osexplorer.OSTreeLabelProvider;
import cl.cavallinux.jisocreator.model.providers.osexplorer.OsTableProvider;

/**
 * Tests para {@link JFaceResourcesManager}.
 *
 * <p>A diferencia de los demas managers/singletons investigados en el Grupo 6
 * ({@code ActionsManager}, {@code IsoExplorerActionsManager},
 * {@code OSExplorerActionsManager}, {@code PreferencesNodeManager},
 * {@code ImageRegister}, {@code GUIManager}), ninguno de los constructores de
 * {@code JFaceResourcesManager} referencia {@code ImageRegister} ni
 * {@code Display} — solo construyen filtros, comparadores, proveedores y
 * listeners de JFace puros (verificado empiricamente con una reproduccion
 * standalone via {@code java} fuera de Surefire: no se emite ninguna
 * advertencia de carga de biblioteca nativa). Es por tanto seguro referenciar
 * este enum de forma headless en cualquier plataforma.
 */
@DisplayName("JFaceResourcesManager tests")
class JFaceResourcesManagerTest {

    @Test
    @DisplayName("OSEXPLORER_INSTANCE should expose the OS explorer's JFace resources")
    void osExplorerInstanceShouldExposeOsExplorerResources() {
        JFaceResourcesManager instance = JFaceResourcesManager.OSEXPLORER_INSTANCE;

        assertEquals(HideHiddenFilesFilter.class, instance.getToggleHiddenFilesFilter().getClass());
        assertEquals(ShowOnlyDirectoriesFilter.class, instance.getShowOnlyDirectoriesFilter().getClass());
        assertEquals(OSDirectoriesComparator.class, instance.getDirectoriesComparator().getClass());
        assertEquals(OSTreeLabelProvider.class, instance.getTreeLabelProvider().getClass());
        assertEquals(OSTreeContentProvider.class, instance.getTreeContentProvider().getClass());
        assertEquals(OsTableProvider.class, instance.getTableProviderAdapter().getClass());
        assertEquals(OSDirectoriesMenuListener.class, instance.getDirectoriesMenuListener().getClass());
        assertEquals(OSExplorerSashFormDoubleClickListener.class, instance.getDoubleClickListener().getClass());
        assertEquals(OSExplorerSashFormSelectionChangedListener.class,
                instance.getSelectionChangedListener().getClass());
    }

    @Test
    @DisplayName("ISOEXPLORER_INSTANCE should expose the ISO explorer's JFace resources")
    void isoExplorerInstanceShouldExposeIsoExplorerResources() {
        JFaceResourcesManager instance = JFaceResourcesManager.ISOEXPLORER_INSTANCE;

        assertNull(instance.getToggleHiddenFilesFilter());
        assertEquals(ShowOnlyIsoDirectoriesFilter.class, instance.getShowOnlyDirectoriesFilter().getClass());
        assertEquals(ITreeNodeDirectoriesFirstComparator.class, instance.getDirectoriesComparator().getClass());
        assertEquals(IsoTreeLabelProvider.class, instance.getTreeLabelProvider().getClass());
        assertEquals(IsoTreeContentProvider.class, instance.getTreeContentProvider().getClass());
        assertEquals(IsoTableProvider.class, instance.getTableProviderAdapter().getClass());
        assertEquals(ISODirectoriesMenuListener.class, instance.getDirectoriesMenuListener().getClass());
        assertEquals(ISOExplorerSashFormDoubleClickListener.class, instance.getDoubleClickListener().getClass());
        assertEquals(ISOExplorerSashFormSelectionChangedListener.class,
                instance.getSelectionChangedListener().getClass());
    }

    @Test
    @DisplayName("Both instances should share the same tree/table SWT style options")
    void bothInstancesShouldShareSameSwtStyleOptions() {
        int expectedOptions = SWT.VIRTUAL | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI;

        assertEquals(expectedOptions, JFaceResourcesManager.OSEXPLORER_INSTANCE.getTreeSWTOptions());
        assertEquals(expectedOptions, JFaceResourcesManager.OSEXPLORER_INSTANCE.getTableSWTOptions());
        assertEquals(expectedOptions, JFaceResourcesManager.ISOEXPLORER_INSTANCE.getTreeSWTOptions());
        assertEquals(expectedOptions, JFaceResourcesManager.ISOEXPLORER_INSTANCE.getTableSWTOptions());
    }

    @Test
    @DisplayName("values() should return both constants and be stable across calls")
    void valuesShouldReturnBothConstantsAndBeStableAcrossCalls() {
        JFaceResourcesManager[] values = JFaceResourcesManager.values();

        assertEquals(2, values.length);
        assertSame(JFaceResourcesManager.OSEXPLORER_INSTANCE, JFaceResourcesManager.valueOf("OSEXPLORER_INSTANCE"));
        assertSame(JFaceResourcesManager.ISOEXPLORER_INSTANCE, JFaceResourcesManager.valueOf("ISOEXPLORER_INSTANCE"));
    }
}
