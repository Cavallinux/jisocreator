package cl.cavallinux.jisocreator.model.dnd;

import java.io.File;
import java.util.List;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.FileTransfer;

import cl.cavallinux.jisocreator.gui.decl.ICompositeCreator;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class JISOCreatorDragSourceAdapter extends DragSourceAdapter {
    private Viewer viewer;

    @Override
    public void dragStart(DragSourceEvent event) {
        log.info("Drag started from viewer: {}", viewer);
        if (viewer != null) {
            IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
            LocalSelectionTransfer.getTransfer().setSelection(selection);
            log.info("LocalSelectionTransfer selection set: {}", selection.toList());
        }
        event.doit = true;
    }

    @Override
    public void dragSetData(DragSourceEvent event) {
        log.info("Setting drag data for event data type: {}", event.dataType);
        boolean isSupported = ICompositeCreator.isDragAndDropTransferTypeSupported(event.dataType);
        
        if (!isSupported) {
            log.warn("Transfer type not supported: {}", event.dataType);
            event.data = null;
            return;
        }

        try {
            IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
            List<File> selectedFiles = selection.stream().filter(File.class::isInstance).map(File.class::cast).toList();
            log.info("Selected items for drag: {}", selectedFiles);

            if (FileTransfer.getInstance().isSupportedType(event.dataType)) {
                String[] absolutePaths = selectedFiles.stream().map(File::getAbsolutePath).toArray(String[]::new);
                log.info("Drag data set as FileTransfer with paths: {}", String.join(", ", absolutePaths));
                event.data = absolutePaths;
            } else if (LocalSelectionTransfer.getTransfer().isSupportedType(event.dataType)) {
                log.info("LocalSelectionTransfer data set (handled in dragStart)");
                event.data = selection.toArray();
            }
        } catch (Exception e) {
            log.error("Error setting drag data", e);
            event.data = null;
        }
    }
}
