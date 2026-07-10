package cl.cavallinux.jisocreator.model.dnd;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;

import cl.cavallinux.jisocreator.action.osexplorer.AddFileAction;
import cl.cavallinux.jisocreator.gui.decl.ICompositeCreator;
import cl.cavallinux.jisocreator.instances.OSExplorerActionsManager;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JISOCreatorViewerDropAdapter extends ViewerDropAdapter {

    @Builder
    protected JISOCreatorViewerDropAdapter(Viewer viewer) {
        super(viewer);
        log.info("ViewerDropAdapter initialized for viewer: {}", viewer);
    }

    @Override
    public boolean performDrop(Object data) {
        try {
            log.info("Performing drop with data type: {}, data: {}", data != null ? data.getClass().getSimpleName() : "null", data);
            
            List<File> files = null;
            
            // Handle FileTransfer (from OS or FileTransfer source)
            if (data instanceof String[]) {
                String[] paths = (String[]) data;
                files = Arrays.stream(paths)
                        .map(File::new)
                        .toList();
                log.info("Drop data interpreted as FileTransfer with {} paths", paths.length);
            }
            // Handle LocalSelectionTransfer (from intra-app drag, e.g., OSExplorer)
            else if (data instanceof Object[]) {
                Object[] objects = (Object[]) data;
                files = Arrays.stream(objects)
                        .filter(obj -> obj instanceof File)
                        .map(obj -> (File) obj)
                        .toList();
                log.info("Drop data interpreted as LocalSelectionTransfer with {} files", files.size());
            }
            // Handle IStructuredSelection directly (fallback)
            else if (data instanceof IStructuredSelection) {
                IStructuredSelection selection = (IStructuredSelection) data;
                files = selection.toList();
                log.info("Drop data interpreted as IStructuredSelection with {} items", files.size());
            }
            
            if (files == null || files.isEmpty()) {
                log.warn("No valid files found in drop data");
                return false;
            }
            
            AddFileAction addFileAction = (AddFileAction) OSExplorerActionsManager.ADDFILEACTION.getAction();
            addFileAction.run(files);
            log.info("AddFileAction executed successfully with {} files", files.size());
            return true;
        } catch (Exception e) {
            log.error("Error performing drop", e);
            return false;
        }
    }

    @Override
    public boolean validateDrop(Object target, int operation, TransferData transferType) {
        try {
            log.info("Validating drop for target: {}, operation: {}, transferType: {}", target, operation, transferType);
            boolean isValid = Arrays.stream(ICompositeCreator.obtainDragAndDropTransferTypes())
                    .anyMatch(transfer -> transfer.isSupportedType(transferType));
            log.info("Drop validation result: {}", isValid);
            return isValid;
        } catch (Exception e) {
            log.error("Error validating drop", e);
            return false;
        }
    }
}
