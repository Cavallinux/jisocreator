package cl.cavallinux.jisocreator.instances;

import cl.cavallinux.jisocreator.util.IOUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IOManager {
    INSTANCE(new IOUtils());

    private IOUtils ioUtils;
}
