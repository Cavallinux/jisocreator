package cl.cavallinux.jisocreator.model.dnd;

import java.io.File;
import java.util.Arrays;
import java.util.List;

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
        AddFileAction addFileAction = (AddFileAction) OSExplorerActionsManager.ADDFILEACTION.getAction();
        List<File> files = Arrays.stream((String[]) data).map(File::new).toList();
        addFileAction.run(files);
        return true;
    }

    @Override
    public boolean validateDrop(Object target, int operation, TransferData transferType) {
        log.info("Validating drop for target: {}, operation: {}, transferType: {}", target, operation, transferType);
        return Arrays.stream(ICompositeCreator.obtainDragAndDropTransferTypes())
                .anyMatch(transfer -> transfer.isSupportedType(transferType));
    }
}
