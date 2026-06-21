package cl.cavallinux.jisocreator.action.main;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import cl.cavallinux.jisocreator.action.decl.IFileManagementAction;
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
    //private Object object;
    private String path;

    public OpenIsoLayoutAction() {
        super("Open layout");
        setToolTipText("Open a iso layout");
        setImageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("open.png"));
    }

    @Override
    public void run() {
        path = obtainAbsolutePathFile("layout.xml", "*".concat(XML_FILE_EXTENSION), XML_DIALOG_TITLE, XML_FILE_NAMES,
                SWT.OPEN);
        if (StringUtils.isNotBlank(path)) {
            populatePath();
        } else {
            log.info("Aborted xml load process");
        }
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
            monitor.beginTask("Opening file", IProgressMonitor.UNKNOWN);
            monitor.subTask("Parsing xml...");
            Optional<IsoFileSystem> deserializedIsoFilesystem = IOManager.INSTANCE.getIsoFilesystemParser()
                    .deserialize(path);
            log.info("Deserialized Isofilesystem: {}", deserializedIsoFilesystem);
            if (deserializedIsoFilesystem.isPresent()) {
                monitor.subTask("Inserting into tree...");
                Display.getDefault().asyncExec(() -> {
                    IsoFileSystem isoFileSystem = deserializedIsoFilesystem.get();
                    GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree()
                            .setInput(isoFileSystem);
                    ITreeNode node = isoFileSystem.getRoot();
                    GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree()
                            .setSelection(new StructuredSelection(node), true);
                    GUIManager.INSTANCE.getMainWindow().getIsoExplorer().getIsoDirectoriesTree().expandToLevel(node, 1);
                });
            } else {
                Display.getDefault().asyncExec(() -> {
                    monitor.setCanceled(true);
                    MessageDialog.openError(GUIManager.INSTANCE.getMainWindow().getShell(), "JISOCREATOR",
                            "XML Selected is not loaded");
                });
            }

        } finally {
            monitor.done();
        }
    }

    private void populatePath() {
        Display.getDefault().asyncExec(() -> {
            try {
                IProgressMonitor progressMonitor = GUIManager.INSTANCE.getMainWindow().getProgressMonitor();
                ModalContext.run(OpenIsoLayoutAction.this, true, progressMonitor, Display.getCurrent());
            } catch (InvocationTargetException | InterruptedException e) {
                log.error("Error loading ISO layout", e);
            }
        });
    }
}