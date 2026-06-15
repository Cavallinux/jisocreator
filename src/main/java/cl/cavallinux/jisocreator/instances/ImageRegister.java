package cl.cavallinux.jisocreator.instances;

import cl.cavallinux.jisocreator.util.ImageUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageRegister {
    INSTANCE(new ImageUtils());

    private ImageUtils imageUtils;
}