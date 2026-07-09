package cl.cavallinux.jisocreator.model.dnd;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;

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
        log.info("Performing drop with data: {}", data);
        return true;
    }

    @Override
    public boolean validateDrop(Object target, int operation, TransferData transferType) {
        log.info("Validating drop for target: {}, operation: {}, transferType: {}", target, operation, transferType);
        return true;
    }

}
