package cl.cavallinux.jisocreator.model.providers.impl.osexplorer;

import java.io.File;
import java.util.Objects;

import org.eclipse.jface.viewers.ITreeContentProvider;

import cl.cavallinux.jisocreator.instances.OSAndIsoExplorerManager;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class OSTreeContentProvider implements ITreeContentProvider {

    @Override
    public Object[] getElements(Object arg0) {
        return getChildren(arg0);
    }

    @Override
    public Object[] getChildren(Object arg0) {
        log.info("Arg received in getChildren: {}", arg0);
        if (arg0 instanceof File) {
            File[] files = ((File) arg0).listFiles();
            // Return empty array instead of null to prevent NullPointerException in tree viewers
            return Objects.nonNull(files) ? files : new File[0];
        } else {
            return OSAndIsoExplorerManager.INSTANCE.getOsExplorer().getRoots();
        }
    }

    @Override
    public Object getParent(Object arg0) {
        log.info("Arg received in getParent: {}", arg0);
        return ((File) arg0).getParentFile();
    }

    @Override
    public boolean hasChildren(Object arg0) {
        log.info("Arg received in hasChildren: {}", arg0);
        File[] files = ((File) arg0).listFiles();
        return Objects.nonNull(files) && files.length > 0;
    }
}