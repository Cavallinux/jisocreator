package cl.cavallinux.jisocreator.model.dnd;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
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
            List<File> files = Collections.emptyList();

            if (data instanceof String[]) {
                String[] paths = (String[]) data;
                files = Arrays.stream(paths).map(File::new).toList();
            } else if (data instanceof IStructuredSelection) {
                IStructuredSelection selection = (IStructuredSelection) data;
                files = selection.stream().filter(obj -> obj instanceof File).map(obj -> (File) obj).toList();
            }

            log.info("Drop data interpreted as {}, with {} items", data.getClass().getSimpleName(), files.size());
            AddFileAction addFileAction = (AddFileAction) OSExplorerActionsManager.ADDFILEACTION.getAction();
            addFileAction.run(files);
            return true;
        } catch (Exception e) {
            log.error("Error performing drop", e);
            return false;
        }
    }

    @Override
    public boolean validateDrop(Object target, int operation, TransferData transferType) {
        try {
            boolean isValid = Arrays.stream(ICompositeCreator.obtainDragAndDropTransferTypes())
                    .anyMatch(transfer -> transfer.isSupportedType(transferType));
            log.info("Validate operation: {}, transferType: {}, validation result: {}", operation,
                    transferType.getClass().getSimpleName(), isValid);
            return isValid;
        } catch (Exception e) {
            log.error("Error validating drop", e);
            return false;
        }
    }
}
