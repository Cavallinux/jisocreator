package cl.cavallinux.jisocreator.action.main;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import cl.cavallinux.jisocreator.gui.dialog.BaseProgressMonitorDialog;
import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenIsoLayoutAction extends Action implements IRunnableWithProgress {
    private Object object;
    private String path;
    private static OpenIsoLayoutAction instance;
    
    static {
        instance = new OpenIsoLayoutAction();
    }

    public OpenIsoLayoutAction() {
        super("Open layout");
        setToolTipText("Open a iso layout");
        setImageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("open.png"));
    }

    @Override
    public void run() {
        try {
            executeOpenFile();
            if (path == null) {
                return;
            }
            ProgressMonitorDialog openProgressDialog = new BaseProgressMonitorDialog(
                    MainWindow.getInstance().getShell());
            openProgressDialog.run(true, false, this);
            openProgressDialog.close();
        } catch (InvocationTargetException | InterruptedException e) {
            log.error("Error opening file", e);
            MessageDialog.openError(MainWindow.getInstance().getShell(), "Error", e.getMessage());
        }
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
            monitor.beginTask("Opening file", IProgressMonitor.UNKNOWN);
            monitor.subTask("Parsing xml...");
            object = IOManager.INSTANCE.getIoUtils().parseXMLFileToObject(path);
            monitor.subTask("Inserting into tree...");
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    IsoExplorerSashForm.getInstance().getIsoDirectoriesTree().setInput(object);
                    ITreeNode node = ((IsoFileSystem) object).getRoot();
                    IsoExplorerSashForm.getInstance().getIsoDirectoriesTree().expandToLevel(node, 1);
                }
            });
        } finally {
            monitor.done();
        }
    }

    public static OpenIsoLayoutAction getInstance() {
        return instance;
    }

    private void executeOpenFile() {
        FileDialog openXMLDialog = new FileDialog(MainWindow.getInstance().getShell(), SWT.OPEN);
        openXMLDialog.setText("Choose a xml file to open");
        openXMLDialog.setOverwrite(true);
        openXMLDialog.setFileName("layout.xml");
        openXMLDialog.setFilterExtensions(new String[] { "*.xml" });
        openXMLDialog.setFilterNames(new String[] { "XML Files" });
        path = openXMLDialog.open();
    }
}