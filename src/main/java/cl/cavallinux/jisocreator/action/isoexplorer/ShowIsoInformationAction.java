package cl.cavallinux.jisocreator.action.isoexplorer;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.gui.dialog.ShowIsoLayoutInformationDialog;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShowIsoInformationAction extends Action {
    public ShowIsoInformationAction() {
        super("Iso layout information", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("properties.png"));
        setToolTipText("Show and/or modify iso layout information");
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

        ShowIsoLayoutInformationDialog dialog = new ShowIsoLayoutInformationDialog(
                GUIManager.INSTANCE.getMainWindow().getShell(), windowTitle, staticInfo, valorInicialVol,
                isoFilesystemSize, applicationID);

        if (dialog.open() == org.eclipse.jface.window.Window.OK) {
            String nuevoVolumeId = dialog.getVolumeIDResponse();
            isoFileSystem.setVolumeID(nuevoVolumeId);
            System.out.println("Operación confirmada. Nuevo Volume ID registrado: " + nuevoVolumeId);
        } else {
            System.out.println("Operación cancelada por el usuario.");
        }
    }
}