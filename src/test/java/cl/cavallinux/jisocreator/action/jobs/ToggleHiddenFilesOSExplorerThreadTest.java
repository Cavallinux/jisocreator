package cl.cavallinux.jisocreator.action.jobs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.model.filters.HideHiddenFilesFilter;

/**
 * Tests para {@link ToggleHiddenFilesOSExplorerThread}.
 *
 * <p>{@code run()} dispara los inicializadores eager de los enums
 * {@code OSExplorerActionsManager}/{@code JFaceResourcesManager} (que cargan
 * imagenes reales via {@code ImageRegister.INSTANCE.getImageUtils()}) y
 * depende de {@code GUIManager.INSTANCE.getMainWindow()}, por lo que no es
 * invocable de forma headless — el mismo tipo de exclusion aplicada a
 * {@code AddFileAction.run()}/{@code PreferencesAction.createPreferenceManager()}
 * en los Grupos 2 y 3. Su metodo privado {@code removeFilters(StructuredViewer)}
 * es, en cambio, logica pura de JFace (manipula la lista de {@link ViewerFilter}
 * del viewer) y se cubre invocandolo por reflexion sobre un
 * {@link StructuredViewer} minimo (sin {@code Control}/{@code Display} reales),
 * siguiendo el mismo patron de subclase que {@code FixedSelectionViewer} en
 * {@code JISOCreatorDragSourceAdapterTest} (Grupo 2).
 */
@DisplayName("ToggleHiddenFilesOSExplorerThread tests")
class ToggleHiddenFilesOSExplorerThreadTest {

    @Test
    @DisplayName("Builder should produce a Thread instance")
    void builderShouldProduceThreadInstance() {
        ToggleHiddenFilesOSExplorerThread thread = ToggleHiddenFilesOSExplorerThread.builder().build();

        assertTrue(thread instanceof Thread);
    }

    @Test
    @DisplayName("removeFilters should remove only HideHiddenFilesFilter instances from the viewer")
    void removeFiltersShouldRemoveOnlyHideHiddenFilesFilterInstances() throws Exception {
        ToggleHiddenFilesOSExplorerThread thread = ToggleHiddenFilesOSExplorerThread.builder().build();
        MinimalStructuredViewer viewer = new MinimalStructuredViewer();
        HideHiddenFilesFilter hideHiddenFilesFilter = HideHiddenFilesFilter.builder().build();
        ViewerFilter otherFilter = new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                return true;
            }
        };
        viewer.addFilter(hideHiddenFilesFilter);
        viewer.addFilter(otherFilter);

        invokeRemoveFilters(thread, viewer);

        List<ViewerFilter> remainingFilters = List.of(viewer.getFilters());
        assertEquals(1, remainingFilters.size());
        assertTrue(remainingFilters.contains(otherFilter));
    }

    private static void invokeRemoveFilters(ToggleHiddenFilesOSExplorerThread target, StructuredViewer viewer)
            throws Exception {
        Method method = ToggleHiddenFilesOSExplorerThread.class.getDeclaredMethod("removeFilters",
                StructuredViewer.class);
        method.setAccessible(true);
        method.invoke(target, viewer);
    }

    /** {@link StructuredViewer} minimo, sin {@code Control}/{@code Display} reales. */
    private static class MinimalStructuredViewer extends StructuredViewer {
        @Override
        protected Widget doFindInputItem(Object element) {
            return null;
        }

        @Override
        protected Widget doFindItem(Object element) {
            return null;
        }

        @Override
        protected void doUpdateItem(Widget item, Object element, boolean fullMap) {
        }

        @Override
        protected List<?> getSelectionFromWidget() {
            return List.of();
        }

        @Override
        protected void internalRefresh(Object element) {
        }

        @Override
        public void reveal(Object element) {
        }

        @Override
        protected void setSelectionToWidget(List list, boolean reveal) {
        }

        @Override
        public Control getControl() {
            return null;
        }

        @Override
        public Object getInput() {
            return null;
        }

        @Override
        public ISelection getSelection() {
            return null;
        }

        @Override
        public void refresh() {
        }

        @Override
        public void setSelection(ISelection selection, boolean reveal) {
        }
    }
}
