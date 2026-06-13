package cl.cavallinux.jisocreator.action.isoexplorer;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.instances.ImageRegister;

public class ShowIsoInformationAction extends Action {
    private static ShowIsoInformationAction instance;
    
    static {
        instance = new ShowIsoInformationAction();
    }

    private ShowIsoInformationAction() {
        super("Open", ImageRegister.INSTANCE.getImageUtils().loadImageDescriptor("properties.png"));
        setToolTipText("Show and/or modify iso layout information");
        setEnabled(false);
    }

    @Override
    public void run() {
    }

    public static ShowIsoInformationAction getInstance() {
        return instance;
    }
}