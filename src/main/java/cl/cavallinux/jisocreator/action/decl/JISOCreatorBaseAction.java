package cl.cavallinux.jisocreator.action.decl;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public abstract class JISOCreatorBaseAction extends Action {
    protected JISOCreatorBaseAction(String message, String tooltip, ImageDescriptor imageDescriptor) {
        super(message);
        setToolTipText(tooltip);
        setImageDescriptor(imageDescriptor);
    }
    
    protected JISOCreatorBaseAction(String message, String tooltip, ImageDescriptor imageDescriptor, int style) {
        super(message, style);
        setToolTipText(tooltip);
        setImageDescriptor(imageDescriptor);
    }
}
