package cl.cavallinux.jisocreator.action.main;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import cl.cavallinux.jisocreator.gui.dialog.BaseProgressMonitorDialog;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SaveAsXMLAction extends Action implements IRunnableWithProgress {
    private static final String XML_FILE_EXTENSION = ".xml";
    private static final String XML_FILE_NAMES = "XML Files";
    private String path;
    private IsoFileSystem iso;

    public SaveAsXMLAction() {
        super("XML Layout", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("xml.png"));
        setToolTipText("Save iso layout as xml file");
    }

    @Override
    public void run() {
        TreeViewer isoDirectoriesTree = GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree();
        iso = (IsoFileSystem) isoDirectoriesTree.getInput();
        iso.setIsoLength();
        iso.setIsoPaths(null);
        setFile(iso);
        if (StringUtils.isNotBlank(path)) {
            try {
                ProgressMonitorDialog saveProgress = new BaseProgressMonitorDialog(
                        GUIManager.INSTANCE.getMainWindow().getShell());
                saveProgress.run(true, false, this);
                saveProgress.close();
            } catch (InvocationTargetException | InterruptedException e) {
                log.error("Error saving layout as xml", e);
            }
        } else {
            log.info("Iso layout xml saving aborted");
            return;
        }

    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        monitor.beginTask("Saving layout", IProgressMonitor.UNKNOWN);
        log.info("Saving layout as xml to path: {}", path);
        if (IOManager.INSTANCE.getIoUtils().saveObjectToXML(iso, path, monitor)) {
            monitor.done();
            log.info("Layout saved successfully as xml to path: {}", path);
        } else {
            throw new InterruptedException("Saving file is not possible, try later.");
        }
    }

    private void setFile(IsoFileSystem isoFileSystem) {
        FileDialog saveXMLDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
        saveXMLDialog.setText("Choose a xml file name to save");
        saveXMLDialog.setOverwrite(true);
        saveXMLDialog.setFileName(isoFileSystem.getVolumeID().concat(XML_FILE_EXTENSION));
        saveXMLDialog.setFilterExtensions(obtainXmlFileDialogExtensions());
        saveXMLDialog.setFilterNames(obtainXmlFileFilterNames());
        path = saveXMLDialog.open();
    }

    private String[] obtainXmlFileFilterNames() {
        List<String> isoFileFilterNamesList = new ArrayList<>();
        isoFileFilterNamesList.add(XML_FILE_NAMES);
        return isoFileFilterNamesList.toArray(String[]::new);
    }

    private String[] obtainXmlFileDialogExtensions() {
        List<String> isoFileExtensionsList = new ArrayList<>();
        isoFileExtensionsList.add("*".concat(XML_FILE_EXTENSION));
        return isoFileExtensionsList.toArray(String[]::new);
    }
}