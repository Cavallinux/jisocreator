package cl.cavallinux.jisocreator.gui.dialog;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ADDFileToIsoLayoutDialog extends ElementTreeSelectionDialog {
    private TreeViewer treeViewer;
    
    @Builder
    public ADDFileToIsoLayoutDialog(Shell parent, IBaseLabelProvider labelProvider,
            ITreeContentProvider contentProvider) {
        super(parent, labelProvider, contentProvider);
    }
    
    @Override
    protected Label createMessageArea(Composite composite) {
        log.info("Creating dialog message area");
        setMessage("Select the destination directory to place the selected files.");
        return super.createMessageArea(composite);
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        log.info("Creating dialog area");
        Control dialogArea = super.createDialogArea(parent);
        treeViewer.expandToLevel(1);
        return dialogArea;
    }
    
    @Override
    protected void configureShell(Shell shell) {
        log.info("Configuring dialog shell");
        super.configureShell(shell);
        shell.setText("Add files to iso layout");
    }
    
    @Override
    protected TreeViewer createTreeViewer(Composite parent) {
        log.info("Creating tree viewer");
        treeViewer = super.createTreeViewer(parent);
        return treeViewer;
    }
}
