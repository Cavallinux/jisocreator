package cl.cavallinux.jisocreator.instances;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.action.osexplorer.AddFileAction;
import cl.cavallinux.jisocreator.action.osexplorer.GoToParentAction;
import cl.cavallinux.jisocreator.action.osexplorer.OpenAction;
import cl.cavallinux.jisocreator.action.osexplorer.RefreshExplorerAction;
import cl.cavallinux.jisocreator.action.osexplorer.ShowHiddenFilesAction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OSExplorerActionsManager {
    ADDFILEACTION(new AddFileAction()), OPENFILEACTION(new OpenAction()), GOTOPARENTACTION(new GoToParentAction()),
    REFRESHACTION(new RefreshExplorerAction()), SHOWHIDDENFILES(new ShowHiddenFilesAction());
    private Action action;
}
