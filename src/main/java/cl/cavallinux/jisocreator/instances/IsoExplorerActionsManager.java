package cl.cavallinux.jisocreator.instances;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.action.isoexplorer.DeleteIsoEntryAction;
import cl.cavallinux.jisocreator.action.isoexplorer.GoToIsoEntryParentAction;
import cl.cavallinux.jisocreator.action.isoexplorer.OpenIsoEntryAction;
import cl.cavallinux.jisocreator.action.isoexplorer.ShowIsoInformationAction;
import cl.cavallinux.jisocreator.gui.i18n.IsoExplorerMessages;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IsoExplorerActionsManager {
    OPENISOENTRY(OpenIsoEntryAction.builder()
            .message(IsoExplorerMessages.isoExplorerOpenEntryActionName)
            .tooltip(IsoExplorerMessages.isoExplorerOpenEntryActionTooltip)
            .imageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("run.png")).build()),
    GOTOISOPARENT(GoToIsoEntryParentAction.builder().message(IsoExplorerMessages.isoExplorerGoToIsoParentActionName)
            .tooltip(IsoExplorerMessages.isoExplorerGoToIsoParentActionTooltip)
            .imageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("up.png")).build()),
    SHOWISOINFO(ShowIsoInformationAction.builder()
            .message(IsoExplorerMessages.isoExplorerShowIsoInfoActionName)
            .tooltip(IsoExplorerMessages.isoExplorerShowIsoInfoActionTooltip)
            .imageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("properties.png")).build()),
    DELETEISOENTRY(DeleteIsoEntryAction.builder().message(IsoExplorerMessages.isoExplorerDeleteEntryActionName)
            .tooltip(IsoExplorerMessages.isoExplorerDeleteEntryActionTooltip)
            .imageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("delete.png")).build());

    private Action action;
}
