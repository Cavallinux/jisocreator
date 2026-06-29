package cl.cavallinux.jisocreator.instances;

import cl.cavallinux.jisocreator.gui.window.MainWindow;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GUIManager {
    INSTANCE(MainWindow.builder().build());

    private MainWindow mainWindow;
}
