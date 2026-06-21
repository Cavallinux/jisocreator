package cl.cavallinux.jisocreator.action.isoexplorer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;

import cl.cavallinux.jisocreator.gui.dialog.ShowIsoLayoutInformationDialog;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShowIsoInformationAction extends Action {
    public ShowIsoInformationAction() {
        super("Iso layout information", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("properties.png"));
        setToolTipText(
                "Show and/or modify iso layout information\n, the volume id field must be minor to 32 chars to be acepted");
        setEnabled(true);
    }

    @Override
    public void run() {
        log.info("Show iso information action executed");
        String windowTitle = getText();
        String staticInfo = getToolTipText();
        IsoFileSystem isoFileSystem = (IsoFileSystem) GUIManager.INSTANCE.getMainWindow().getIsoExplorer()
                .getIsoDirectoriesTree().getInput();
        String valorInicialVol = isoFileSystem.getVolumeID();
        String applicationID = isoFileSystem.getApplicationID();
        String isoFilesystemSize = String.valueOf(isoFileSystem.getIsoLength());
        String publisherID = isoFileSystem.getPublisherID();
        
        ShowIsoLayoutInformationDialog dialog = new ShowIsoLayoutInformationDialog(
                GUIManager.INSTANCE.getMainWindow().getShell(), windowTitle, staticInfo, valorInicialVol,
                isoFilesystemSize, applicationID, publisherID);
        
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