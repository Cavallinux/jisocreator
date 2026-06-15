package cl.cavallinux.jisocreator.action.isoexplorer;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.instances.ImageRegister;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShowIsoInformationAction extends Action {
    public ShowIsoInformationAction() {
        super("Open", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("properties.png"));
        setToolTipText("Show and/or modify iso layout information");
        setEnabled(false);
    }

    @Override
    public void run() {
        log.info("Show iso information action executed");
    }
}