package cl.cavallinux.jisocreator.instances;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.action.isoexplorer.DeleteIsoEntryAction;
import cl.cavallinux.jisocreator.action.isoexplorer.OpenIsoEntryAction;
import cl.cavallinux.jisocreator.action.isoexplorer.ShowIsoInformationAction;
import cl.cavallinux.jisocreator.action.osexplorer.GoToParentAction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IsoExplorerActionsManager {
    OPENISOENTRY(OpenIsoEntryAction.builder().build()), GOTOISOPARENT(GoToParentAction.builder().build()),
    SHOWISOINFO(ShowIsoInformationAction.builder().build()), DELETEISOENTRY(DeleteIsoEntryAction.builder().build());

    private Action action;
}
