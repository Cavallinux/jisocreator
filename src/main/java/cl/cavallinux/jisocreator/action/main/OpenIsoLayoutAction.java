package cl.cavallinux.jisocreator.action.main;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import cl.cavallinux.jisocreator.action.decl.IFileManagementAction;
import cl.cavallinux.jisocreator.gui.dialog.BaseProgressMonitorDialog;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.IOManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenIsoLayoutAction extends Action implements IRunnableWithProgress, IFileManagementAction {
    private static final String XML_FILE_EXTENSION = ".xml";
    private static final String XML_FILE_NAMES = "XML Files";
    private static final String XML_DIALOG_TITLE = "Choose a xml file name to load";
    private Object object;
    private String path;

    public OpenIsoLayoutAction() {
        super("Open layout");
        setToolTipText("Open a iso layout");
        setImageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("open.png"));
    }

    @Override
    public void run() {
        try {
            path = obtainAbsolutePathFile("layout.xml", "*".concat(XML_FILE_EXTENSION), XML_DIALOG_TITLE,
                    XML_FILE_NAMES, SWT.OPEN);
            if (StringUtils.isNotBlank(path)) {
                ProgressMonitorDialog openProgressDialog = new BaseProgressMonitorDialog(
                        GUIManager.INSTANCE.getMainWindow().getShell());
                openProgressDialog.run(true, false, this);
                openProgressDialog.close();
            } else {
                log.info("Aborted xml load process");
            }
            
        } catch (InvocationTargetException | InterruptedException e) {
            log.error("Error opening file", e);
            MessageDialog.openError(GUIManager.INSTANCE.getMainWindow().getShell(), "Error", e.getMessage());
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
                    GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree().setInput(object);
                    ITreeNode node = ((IsoFileSystem) object).getRoot();
                    GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree()
                            .setSelection(new StructuredSelection(node), true);
                    GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree().expandToLevel(node, 1);
                }
            });
        } finally {
            monitor.done();
        }
    }
}