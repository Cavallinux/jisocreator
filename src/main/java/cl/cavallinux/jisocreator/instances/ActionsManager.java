package cl.cavallinux.jisocreator.instances;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.action.main.AboutAction;
import cl.cavallinux.jisocreator.action.main.ExitApplicationAction;
import cl.cavallinux.jisocreator.action.main.MainAction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActionsManager {
    MAINACTION(new MainAction()), EXITACTION(new ExitApplicationAction()),
    ABOUTACTION(new AboutAction());

    private Action action;
}