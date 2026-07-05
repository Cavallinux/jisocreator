package cl.cavallinux.jisocreator.action.osexplorer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import cl.cavallinux.jisocreator.action.decl.JISOCreatorBaseAction;
import cl.cavallinux.jisocreator.action.jobs.ToggleHiddenFilesOSExplorerThread;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShowHiddenFilesAction extends JISOCreatorBaseAction {
    @Builder
    public ShowHiddenFilesAction(String message, String tooltip, ImageDescriptor imageDescriptor, int style) {
        super(message, tooltip, imageDescriptor, style);
    }

    @Override
    public void run() {
        log.info("Showing of hiding hidden files");
        Display.getCurrent().asyncExec(ToggleHiddenFilesOSExplorerThread.builder().build());
    }
}