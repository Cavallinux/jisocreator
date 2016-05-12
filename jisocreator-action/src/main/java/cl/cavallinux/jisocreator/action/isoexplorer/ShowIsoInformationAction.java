package cl.cavallinux.jisocreator.action.isoexplorer;

import org.eclipse.jface.action.Action;

import cl.cavallinux.jisocreator.util.ImageUtils;

public class ShowIsoInformationAction extends Action {
    private static ShowIsoInformationAction instance;

    private ShowIsoInformationAction() {
	super("Open", ImageUtils.getInstance().loadImageDescriptor("properties.png"));
	setToolTipText("Show and/or modify iso layout information");
	setEnabled(false);
    }

    @Override
    public void run() {
    }

    public static ShowIsoInformationAction getInstance() {
	if (instance == null) {
	    instance = new ShowIsoInformationAction();
	}
	return instance;
    }
}