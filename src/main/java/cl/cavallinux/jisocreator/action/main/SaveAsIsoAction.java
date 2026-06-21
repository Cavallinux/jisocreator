package cl.cavallinux.jisocreator.action.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;

import cl.cavallinux.jisocreator.action.decl.IFileManagementAction;
import cl.cavallinux.jisocreator.action.jobs.SaveISO9660ImageThread;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.util.IOUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class SaveAsIsoAction extends Action implements IFileManagementAction {
    private static final String ISO_FILE_EXTENSION = ".iso";
    private static final String ISO_FILE_NAMES = "ISO9660 CD-ROM Files";
    private static final String ISO_DIALOG_TITLE = "Choose a iso file name to save";
    private String inputXMLLayoutFile;
    private String outputISOFile;
    private boolean commandLineMode;

    public SaveAsIsoAction() {
        super("ISO9660 File", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("x-cd-image.png"));
        setToolTipText("Save the actual layout into a ISO9660 file");
        this.commandLineMode = false;
    }

    @Override
    public void run() {
        IsoFileSystem root = obtainIsoFileSystem();
        if (Objects.nonNull(root)) {
            String isoFilePath = obtainIsoFilePath(root);
            if (StringUtils.isNotBlank(isoFilePath)) {
                parseAndStartIsoSaveProcess(root, isoFilePath);
            } else {
                log.info("File not selected, aborting saving");
                return;
            }
        } else {
            log.error("Error loading iso filesystem");
        }
    }

    private void parseAndStartIsoSaveProcess(IsoFileSystem root, String isoFilePath) {
        try {
            deleteIsoFileIfExists(isoFilePath);
            root.parse();
            List<String> mkisofsCommand = setMkisofsCommand(isoFilePath, root);
            ProcessBuilder mkisofsProcessBuilder = new ProcessBuilder(mkisofsCommand);
            log.info("Process to be started: {}", String.join(" ", mkisofsProcessBuilder.command()));
            Process isoProcess = mkisofsProcessBuilder.start();
            SaveISO9660ImageThread saveThread = SaveISO9660ImageThread.builder().build();
            saveThread.setSaveProgressInputStream(isoProcess.getErrorStream());
            saveThread.setMkisofsProcess(isoProcess);
            saveThread.setCommandLineMode(commandLineMode);
            saveThread.start();
        } catch (IOException e) {
            log.error("Error saving iso filesystem", e);
        }
    }

    private IsoFileSystem obtainIsoFileSystem() {
        if (commandLineMode) {
            return (IsoFileSystem) IOManager.INSTANCE.getIsoFilesystemParser().deserialize(inputXMLLayoutFile).get();
        } else {
            return (IsoFileSystem) GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree()
                    .getInput();
        }
    }

    private String obtainIsoFilePath(IsoFileSystem isoFileSystem) {
        if (commandLineMode) {
            return outputISOFile;
        } else {
            return obtainAbsolutePathFile(isoFileSystem.getVolumeID().concat(ISO_FILE_EXTENSION),
                    "*".concat(ISO_FILE_EXTENSION), ISO_DIALOG_TITLE, ISO_FILE_NAMES, SWT.SAVE);
        }
    }

    private void deleteIsoFileIfExists(String path) {
        if (new File(path).exists()) {
            new File(path).delete();
        }
    }

    private List<String> setMkisofsCommand(String isoOutputFileAbsolutePath, IsoFileSystem isoFileSystem) {
        List<String> mkisofsCommand = new ArrayList<>();
        IOUtils storeInstance = IOManager.INSTANCE.getIoUtils();
        PreferenceStore preferenceStore = storeInstance.getStore();
        mkisofsCommand.add(preferenceStore.getString("mkisofs.path"));
        mkisofsCommand.add("-publisher");
        mkisofsCommand.add(isoFileSystem.getPublisherID());
        mkisofsCommand.add("-A");
        mkisofsCommand.add(isoFileSystem.getApplicationID());
        mkisofsCommand.add("-V");
        mkisofsCommand.add(isoFileSystem.getVolumeID());
        mkisofsCommand.add("-graft-points");
        mkisofsCommand.add("-gui");
        if (preferenceStore.getBoolean("mkisofs.joliet.use")) {
            mkisofsCommand.add("-J");
            mkisofsCommand.add("-joliet-long");
        }
        if (preferenceStore.getBoolean("mkisofs.rockridge.use")) {
            mkisofsCommand.add("-r");
        }
        if (preferenceStore.getBoolean("mkisofs.symlinks.follow")) {
            mkisofsCommand.add("-f");
        }
        mkisofsCommand.add("-l");
        mkisofsCommand.add("-D");
        mkisofsCommand.add("-no-bak");
        mkisofsCommand.add("-iso-level");
        mkisofsCommand.add(preferenceStore.getString("mkisofs.iso.level"));
        mkisofsCommand.add("-o");
        mkisofsCommand.add(isoOutputFileAbsolutePath);
        mkisofsCommand.addAll(isoFileSystem.getPaths());
        
        return mkisofsCommand;
    }
}