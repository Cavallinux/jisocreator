package cl.cavallinux.jisocreator.model.dnd;

import java.io.File;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;

import cl.cavallinux.jisocreator.gui.decl.ICompositeCreator;
import cl.cavallinux.jisocreator.instances.GUIManager;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class JISOCreatorDragSourceAdapter extends DragSourceAdapter {
    @Override
    @SuppressWarnings("unchecked")
    public void dragSetData(DragSourceEvent event) {
        boolean isSupported = ICompositeCreator.isDragAndDropTransferTypeSupported(event.dataType);
        log.info("Setting drag data for event data type, is supported? {} : {}", event.dataType, isSupported);
        if (isSupported) {
            IStructuredSelection selection = (IStructuredSelection) GUIManager.INSTANCE.getMainWindow().getOsExplorer()
                    .getOsDirectoriesTable().getSelection();
            log.info("Selected items for drag: {}", selection.toList());
            List<File> listFiles = selection.toList();
            String[] rutasAbsolutas = listFiles.stream().map(File::getAbsolutePath).toArray(String[]::new);
            log.info("Drag data set with absolute paths: {}", String.join(", ", rutasAbsolutas));
            event.data = rutasAbsolutas;
        }
    }
}
