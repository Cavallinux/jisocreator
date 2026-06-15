package cl.cavallinux.jisocreator.action.isoexplorer;

import java.io.File;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;
import cl.cavallinux.jisocreator.instances.GUIManager;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class OpenIsoEntryAction extends Action /*implements IDoubleClickListener, ISelectionChangedListener*/ {
    private ITreeNode node;

    public OpenIsoEntryAction() {
        super("Open", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("run.png"));
        setToolTipText("Open a file or folder, in the layout");
        setEnabled(false);
    }

    @Override
    public void run() {
        File element = (File) node.getElement();
        if (element.isFile()) {
            OSAndIsoExplorerManager.INSTANCE.getOsExplorer().launch(element.toPath());
        } else {
            IsoExplorerSashForm isoSashFormInstance = GUIManager.INSTANCE.getMainWindow().getIsoExplorer();
            isoSashFormInstance.refresh(node);
        }
    }
}