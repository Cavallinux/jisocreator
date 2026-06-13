package cl.cavallinux.jisocreator.instances;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.action.isoexplorer.DeleteIsoEntryAction;
import cl.cavallinux.jisocreator.action.isoexplorer.GoToIsoEntryParentAction;
import cl.cavallinux.jisocreator.action.isoexplorer.OpenIsoEntryAction;
import cl.cavallinux.jisocreator.action.isoexplorer.ShowIsoInformationAction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IsoExplorerActionsManager {
    DELETEISOENTRY(new DeleteIsoEntryAction()), GOTOISOPARENT(new GoToIsoEntryParentAction()),
    OPENISOENTRY(new OpenIsoEntryAction()), SHOWISOINFO(new ShowIsoInformationAction());

    private Action action;
}
