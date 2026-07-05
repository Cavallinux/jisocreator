package cl.cavallinux.jisocreator.instances;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.action.osexplorer.AddFileAction;
import cl.cavallinux.jisocreator.action.osexplorer.GoToParentAction;
import cl.cavallinux.jisocreator.action.osexplorer.OpenAction;
import cl.cavallinux.jisocreator.action.osexplorer.RefreshExplorerAction;
import cl.cavallinux.jisocreator.action.osexplorer.ShowHiddenFilesAction;
import cl.cavallinux.jisocreator.gui.i18n.OSExplorerMessages;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OSExplorerActionsManager {
    ADDFILEACTION(AddFileAction.builder()
            .message(OSExplorerMessages.osExplorerAddActionName)
            .tooltip(OSExplorerMessages.osExplorerAddActionToolTip)
            .imageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("add.png")).build()),
    OPENFILEACTION(OpenAction.builder()
            .message(OSExplorerMessages.osExplorerOpenActionName)
            .tooltip(OSExplorerMessages.osExplorerOpenActionToolTip)
            .imageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("run.png")).build()),
    GOTOPARENTACTION(GoToParentAction.builder()
            .message(OSExplorerMessages.osExplorerGoToParentActionName)
            .tooltip(OSExplorerMessages.osExplorerGoToParentActionToolTip)
            .imageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("up.png")).build()),
    REFRESHACTION(RefreshExplorerAction.builder()
            .message(OSExplorerMessages.osExplorerRefreshActionName)
            .tooltip(OSExplorerMessages.osExplorerRefreshActionToolTip)
            .imageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("refresh.png")).build()),
    SHOWHIDDENFILES(ShowHiddenFilesAction.builder()
            .message(OSExplorerMessages.osExplorerShowHiddenActionName)
            .tooltip(OSExplorerMessages.osExplorerShowHiddenActionToolTip)
            .imageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("showhidden.svg"))
            .style(Action.AS_CHECK_BOX).build());

    private Action action;
}
