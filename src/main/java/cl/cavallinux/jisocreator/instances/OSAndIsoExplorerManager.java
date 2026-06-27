package cl.cavallinux.jisocreator.instances;

import cl.cavallinux.jisocreator.model.osexplorer.OSExplorer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OSAndIsoExplorerManager {
    INSTANCE(OSExplorer.builder().build());

    private OSExplorer osExplorer;
}
