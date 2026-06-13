package cl.cavallinux.jisocreator.action.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import cl.cavallinux.jisocreator.action.jobs.SaveISO9660ImageThread;
import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.util.IOUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SaveAsIsoAction extends Action {
    private List<String> mkisofsCommand;

    public SaveAsIsoAction() {
        super("ISO9660 File", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("x-cd-image.png"));
        setToolTipText("Save the actual layout into a ISO9660 file");
    }

    @Override
    public void run() {
        try {
            FileDialog openXMLDialog = new FileDialog(GUIManager.INSTANCE.getMainWindow().getShell(), SWT.SAVE);
            openXMLDialog.setText("Choose a iso file to save");
            openXMLDialog.setOverwrite(true);
            openXMLDialog.setFileName("iso.iso");
            openXMLDialog.setFilterExtensions(new String[] { "*.iso" });
            openXMLDialog.setFilterNames(new String[] { "ISO9660 CD-ROM Files" });
            String path = openXMLDialog.open();
            if (StringUtils.isNotBlank(path)) {
                deleteIsoFileIfExists(path);
                IsoFileSystem root = obtainIsoFileSystem();
                root.parse();
                setMkisofsCommand(path, root.getPaths());
                ProcessBuilder mkisofsProcessBuilder = new ProcessBuilder(mkisofsCommand);
                Process isoProcess = mkisofsProcessBuilder.start();
                SaveISO9660ImageThread saveThread = new SaveISO9660ImageThread(isoProcess.getErrorStream());
                saveThread.start();
            }
        } catch (IOException e) {
            log.error("Error executing command", e);
        }
    }

    private IsoFileSystem obtainIsoFileSystem() {
        return (IsoFileSystem) IsoExplorerSashForm.getInstance().getIsoDirectoriesTree().getInput();
    }

    private void deleteIsoFileIfExists(String path) {
        if (new File(path).exists()) {
            new File(path).delete();
        }
    }

    private void setMkisofsCommand(String path, List<String> rootPaths) {
        mkisofsCommand = new ArrayList<String>();
        IOUtils storeInstance = IOManager.INSTANCE.getIoUtils();
        PreferenceStore preferenceStore = storeInstance.getStore();
        mkisofsCommand.add(preferenceStore.getString("mkisofs.path"));
        mkisofsCommand.add("-graft-points");
        mkisofsCommand.add("-gui");
        if (preferenceStore.getBoolean("mkisofs.joliet.use")) {
            mkisofsCommand.add("-J");
            mkisofsCommand.add("-joliet-long");
            if (preferenceStore.getBoolean("mkisofs.rockridge.use")) {
                mkisofsCommand.add("-r");
            }
        }

        if (preferenceStore.getBoolean("mkisofs.symlinks.follow")) {
            mkisofsCommand.add("-f");
        }
        mkisofsCommand.add("-l");
        mkisofsCommand.add("-D");
        mkisofsCommand.add("-no-bak");
        mkisofsCommand.add("-iso-level");
        mkisofsCommand.add("2");
        mkisofsCommand.add("-o");
        mkisofsCommand.add(path);
        mkisofsCommand.addAll(rootPaths);
    }
}