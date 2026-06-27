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
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActionsManager {
    MAINACTION(new MainAction()), EXITACTION(new ExitApplicationAction()), ABOUTACTION(new AboutAction()),
    PREFERENCESACTION(PreferencesAction.builder().build()), SAVEASXMLACTION(new SaveAsXMLAction()),
    SAVEASISOACTION(new SaveAsIsoAction()), NEWISOLAYOUTACTION(new NewIsoLayoutAction()),
    OPENISOLAYOUTACTION(new OpenIsoLayoutAction()), LOADISOFROMLAYOUT(new LoadCommandLineISOLayoutAction());

    private Action action;
}