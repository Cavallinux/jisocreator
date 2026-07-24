package cl.cavallinux.jisocreator.model.dnd;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Widget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;

import cl.cavallinux.jisocreator.testsupport.SwtPlatformAssumptions;

/**
 * Tests para {@link JISOCreatorDragSourceAdapter}.
 *
 * <p>{@code DragSourceEvent} solo puede construirse a través de un
 * {@code org.eclipse.swt.dnd.DNDEvent} (constructor de paquete privado en
 * SWT) que a su vez exige un {@code Widget} no nulo como origen del evento.
 * Este test usa un pequeño helper reflexivo ({@link #newDragSourceEvent()})
 * apoyado en Objenesis (dependencia transitiva de Mockito, ya presente en el
 * classpath del proyecto) para instanciar un {@code Widget} vacío sin
 * necesidad de un {@code Display}/servidor X real, permitiendo probar la
 * lógica real de {@code dragStart}/{@code dragSetData} en modo headless.</p>
 *
 * <p>Sin embargo, los tests si invocan {@code FileTransfer.getInstance()} y
 * {@code LocalSelectionTransfer.getTransfer()}, cuyo registro de tipos DnD
 * requiere que el fragmento nativo de SWT en el classpath coincida con el
 * sistema operativo real del host (ver {@link SwtPlatformAssumptions}). Cada
 * test llama primero a
 * {@link SwtPlatformAssumptions#assumeNativePlatformMatches()} para omitirse
 * (skip) de forma fluida si el profile Maven activo no coincide con el host,
 * en vez de invocar la API nativa y colapsar la JVM.</p>
 */
@DisplayName("JISOCreatorDragSourceAdapter tests")
class JISOCreatorDragSourceAdapterTest {

    @BeforeEach
    void checkNativePlatform() {
        SwtPlatformAssumptions.assumeNativePlatformMatches();
    }

    /** {@link Viewer} mínimo que expone una selección fija, sin depender de un {@code Display} real. */
    private static class FixedSelectionViewer extends Viewer {
        private final ISelection selection;

        FixedSelectionViewer(ISelection selection) {
            this.selection = selection;
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
            return selection;
        }

        @Override
        public void refresh() {
        }

        @Override
        public void setInput(Object input) {
        }

        @Override
        public void setSelection(ISelection selection, boolean reveal) {
        }
    }

    @Test
    @DisplayName("dragStart should accept the event and publish the viewer selection to LocalSelectionTransfer")
    void dragStartShouldAcceptEventAndPublishSelection() throws Exception {
        File selectedFile = new File("/tmp/jisocreator-dnd-test.txt");
        IStructuredSelection selection = new StructuredSelection(List.of(selectedFile));
        Viewer viewer = new FixedSelectionViewer(selection);
        JISOCreatorDragSourceAdapter adapter = JISOCreatorDragSourceAdapter.builder().viewer(viewer).build();

        DragSourceEvent event = newDragSourceEvent();
        adapter.dragStart(event);

        assertTrue(event.doit);
        assertEquals(List.of(selectedFile),
                ((IStructuredSelection) LocalSelectionTransfer.getTransfer().getSelection()).toList());
    }

    @Test
    @DisplayName("dragSetData should populate FileTransfer data with absolute paths of the selected files")
    void dragSetDataShouldPopulateFileTransferData() throws Exception {
        File selectedFile = new File("/tmp/jisocreator-dnd-test.txt");
        IStructuredSelection selection = new StructuredSelection(List.of(selectedFile));
        Viewer viewer = new FixedSelectionViewer(selection);
        JISOCreatorDragSourceAdapter adapter = JISOCreatorDragSourceAdapter.builder().viewer(viewer).build();

        DragSourceEvent event = newDragSourceEvent();
        event.dataType = fileTransferTransferData();

        adapter.dragSetData(event);

        assertArrayEquals(new String[] { selectedFile.getAbsolutePath() }, (String[]) event.data);
    }

    @Test
    @DisplayName("dragSetData should clear event data for an unsupported transfer type")
    void dragSetDataShouldClearDataForUnsupportedTransferType() throws Exception {
        IStructuredSelection selection = new StructuredSelection(List.of());
        Viewer viewer = new FixedSelectionViewer(selection);
        JISOCreatorDragSourceAdapter adapter = JISOCreatorDragSourceAdapter.builder().viewer(viewer).build();

        DragSourceEvent event = newDragSourceEvent();
        TransferData unsupported = new TransferData();
        unsupported.type = Integer.MAX_VALUE;
        event.dataType = unsupported;

        adapter.dragSetData(event);

        assertNull(event.data);
    }

    private static TransferData fileTransferTransferData() throws Exception {
        Method getTypeIds = FileTransfer.class.getDeclaredMethod("getTypeIds");
        getTypeIds.setAccessible(true);
        int[] typeIds = (int[]) getTypeIds.invoke(FileTransfer.getInstance());
        TransferData transferData = new TransferData();
        transferData.type = typeIds[0];
        return transferData;
    }

    /** Builds a real, usable {@link DragSourceEvent} without requiring an SWT {@code Display}. */
    private static DragSourceEvent newDragSourceEvent() throws Exception {
        Widget placeholderWidget = new ObjenesisStd().newInstance(Table.class);

        Class<?> dndEventClass = Class.forName("org.eclipse.swt.dnd.DNDEvent");
        Constructor<?> dndEventConstructor = dndEventClass.getDeclaredConstructor();
        dndEventConstructor.setAccessible(true);
        Object dndEvent = dndEventConstructor.newInstance();
        ((Event) dndEvent).widget = placeholderWidget;

        Constructor<DragSourceEvent> dragSourceEventConstructor = DragSourceEvent.class
                .getDeclaredConstructor(dndEventClass);
        dragSourceEventConstructor.setAccessible(true);
        return dragSourceEventConstructor.newInstance(dndEvent);
    }
}
