package cl.cavallinux.jisocreator.action.main;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;

import cl.cavallinux.jisocreator.action.decl.IFileManagementAction;
import cl.cavallinux.jisocreator.gui.dialog.BaseProgressMonitorDialog;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SaveAsXMLAction extends Action implements IRunnableWithProgress, IFileManagementAction {
    private static final String XML_FILE_EXTENSION = ".xml";
    private static final String XML_FILE_NAMES = "XML Files";
    private static final String XML_DIALOG_TITLE = "Choose a xml file name to save";
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
        path = obtainAbsolutePathFile(iso.getVolumeID().concat(XML_FILE_EXTENSION), "*".concat(XML_FILE_EXTENSION),
                XML_DIALOG_TITLE, XML_FILE_NAMES, SWT.SAVE);
        // setFile(iso);
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

}