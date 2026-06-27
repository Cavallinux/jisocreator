package cl.cavallinux.jisocreator.action.isoexplorer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import cl.cavallinux.jisocreator.gui.dialog.ShowIsoLayoutInformationDialog;
import cl.cavallinux.jisocreator.gui.i18n.IsoExplorerMessages;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShowIsoInformationAction extends Action {
    @Builder
    private ShowIsoInformationAction() {
        super(IsoExplorerMessages.isoExplorerShowIsoInfoActionName,
                ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("properties.png"));
        setToolTipText(IsoExplorerMessages.isoExplorerShowIsoInfoActionTooltip);
        setEnabled(true);
    }

    @Override
    public void run() {
        log.info("Show iso information action executed");
        IsoFileSystem isoFileSystem = (IsoFileSystem) GUIManager.INSTANCE.getMainWindow().getIsoExplorer()
                .getIsoDirectoriesTree().getInput();
        Shell shell = GUIManager.INSTANCE.getMainWindow().getShell();
        ShowIsoLayoutInformationDialog dialog = ShowIsoLayoutInformationDialog.builder().parentShell(shell)
                .isoFileSystem(isoFileSystem).build();

        switch (dialog.open()) {
        case Window.OK: {
            String nuevoVolumeId = dialog.getVolumeIDResponse();
            isoFileSystem.setVolumeID(nuevoVolumeId);
            log.info("Confirmed operation, new volume id: {} ", isoFileSystem.getVolumeID());
            break;
        }
        default:
            log.info("User cancelled operation");
        }
    }
}