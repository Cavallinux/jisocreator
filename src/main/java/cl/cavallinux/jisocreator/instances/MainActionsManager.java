package cl.cavallinux.jisocreator.instances;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.action.main.MainAction;
import cl.cavallinux.jisocreator.action.main.SaveAsIsoAction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MainActionsManager {
    MAINACTION(MainAction.builder()
            .parser(CommandLineParserManager.INSTANCE.getParser()).layoutFilePath(StringUtils.EMPTY).build()),
    SAVEASISOACTION(SaveAsIsoAction.builder().build());
    private Action action;
}
