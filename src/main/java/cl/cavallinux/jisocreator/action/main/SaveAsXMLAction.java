package cl.cavallinux.jisocreator.action.main;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import cl.cavallinux.jisocreator.gui.dialog.BaseProgressMonitorDialog;
import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.util.IOUtils;
import cl.cavallinux.jisocreator.util.ImageUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SaveAsXMLAction extends Action implements IRunnableWithProgress {
    private String path;
    private IsoFileSystem iso;
    private static SaveAsXMLAction instance;
    static {
        instance = new SaveAsXMLAction();
    }

    private SaveAsXMLAction() {
        super("XML Layout", ImageUtils.getInstance().loadImageDescriptor("xml.png"));
        setToolTipText("Save iso layout as xml file");
    }

    @Override
    public void run() {
        setFile();
        if (path == null) {
            return;
        }
        iso = (IsoFileSystem) IsoExplorerSashForm.getInstance().getIsoDirectoriesTree().getInput();
        iso.setIsoLength();
        iso.setIsoPaths(null);
        try {
            ProgressMonitorDialog saveProgress = new BaseProgressMonitorDialog(Display.getDefault().getActiveShell());
            saveProgress.run(true, false, this);
            saveProgress.close();
        } catch (InvocationTargetException | InterruptedException e) {
            log.error("Error saving layout as xml", e);
        }

    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        monitor.beginTask("Saving layout", IProgressMonitor.UNKNOWN);
        log.info("Saving layout as xml to path: {}", path);
        if (IOUtils.getInstance().saveObjectToXML(iso, path, monitor)) {
            monitor.done();
            log.info("Layout saved successfully as xml to path: {}", path);
        } else {
            throw new InterruptedException("Saving file is not possible, try later.");
        }
    }

    public static SaveAsXMLAction getInstance() {
        return instance;
    }

    private void setFile() {
        FileDialog saveXMLDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
        saveXMLDialog.setText("Choose a xml file name to save");
        saveXMLDialog.setOverwrite(true);
        saveXMLDialog.setFileName("layout.xml");
        saveXMLDialog.setFilterExtensions(new String[] { "*.xml" });
        saveXMLDialog.setFilterNames(new String[] { "XML Files" });
        path = saveXMLDialog.open();
    }
}