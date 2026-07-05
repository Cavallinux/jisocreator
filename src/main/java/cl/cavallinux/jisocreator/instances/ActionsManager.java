package cl.cavallinux.jisocreator.instances;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.action.main.AboutAction;
import cl.cavallinux.jisocreator.action.main.ExitApplicationAction;
import cl.cavallinux.jisocreator.action.main.LoadCommandLineISOLayoutAction;
import cl.cavallinux.jisocreator.action.main.MainAction;
import cl.cavallinux.jisocreator.action.main.NewIsoLayoutAction;
import cl.cavallinux.jisocreator.action.main.OpenIsoLayoutAction;
import cl.cavallinux.jisocreator.action.main.PreferencesAction;
import cl.cavallinux.jisocreator.action.main.SaveAsIsoAction;
import cl.cavallinux.jisocreator.action.main.SaveAsXMLAction;
import cl.cavallinux.jisocreator.gui.i18n.MainActionsMessages;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActionsManager {
    MAINACTION(MainAction.builder().build()),
    EXITACTION(ExitApplicationAction.builder().message(MainActionsMessages.exitActionName)
            .tooltip(MainActionsMessages.exitActionTooltip)
            .imageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("exit.png")).build()),
    ABOUTACTION(AboutAction.builder().message(MainActionsMessages.aboutActionName)
            .tooltip(MainActionsMessages.aboutActionTooltip)
            .imageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("about.png")).build()),
    PREFERENCESACTION(PreferencesAction.builder().message(MainActionsMessages.preferencesActionName)
            .tooltip(MainActionsMessages.preferencesActionTooltip)
            .imageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("preferences.png")).build()),
    SAVEASXMLACTION(SaveAsXMLAction.builder().message(MainActionsMessages.saveAsXMLActionName)
            .toolTip(MainActionsMessages.saveAsXMLActionTooltip)
            .imageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("xml.png")).build()),
    SAVEASISOACTION(SaveAsIsoAction.builder().message(MainActionsMessages.saveAsIsoActionName)
            .tooltip(MainActionsMessages.saveAsIsoActionTooltip)
            .imageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("x-cd-image.png")).build()),
    NEWISOLAYOUTACTION(NewIsoLayoutAction.builder()
            .message(MainActionsMessages.newIsoLayoutActionName)
            .tooltip(MainActionsMessages.newIsoLayoutActionTooltip)
            .imageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("new.png")).build()),
    OPENISOLAYOUTACTION(OpenIsoLayoutAction.builder().message(MainActionsMessages.openIsoLayoutActionName)
            .tooltip(MainActionsMessages.openIsoLayoutActionTooltip)
            .imageDescriptor(ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("open.png")).build()),
    LOADISOFROMLAYOUT(LoadCommandLineISOLayoutAction.builder().build());

    private Action action;
}