package cl.cavallinux.jisocreator.model.osexplorer;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.eclipse.swt.program.Program;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OSExplorer {
    private File[] roots;
    private static OSExplorer instance;
    private static final String FOLDER_TYPE = "Folder";
    private static final String FILE_TYPE = "File";
    private static final int NO_EXTENSION_DOT = -1;
    private static final char EXTENSION_DOT_CHAR = '.';
    
    static {
        instance = newInstance();
    }

    public static OSExplorer getInstance() {
        return instance;
    }

    private OSExplorer(File[] roots) {
        log.info("OS: {}, file roots: {}", System.getProperty("os.name"), roots);
        this.setRoots(roots);
    }

    private OSExplorer() {
        this(File.listRoots());
    }

    private static OSExplorer newInstance() {
        return new OSExplorer();
    }

    public boolean launch(File file) {
        return Program.launch(file.getAbsolutePath());
    }

    public String getName(File file) {
        return file.getName();
    }

    public String getAbsolutePath(File file) {
        return file.getAbsolutePath();
    }

    public String length(File file) {
        return Long.toString(file.length());
    }

    public String lastModified(File file) {
        return DateFormat.getDateTimeInstance().format(new Date(file.lastModified()));
    }

    public String getFileType(File file) {
        String extension = getExtension(file);
        if (Strings.CI.contains(extension, FOLDER_TYPE)) {
            return FOLDER_TYPE;
        } else if (StringUtils.isBlank(extension)) {
            return FILE_TYPE;
        } else {
            Program program = Program.findProgram(extension);
            return Objects.nonNull(program) ? program.getName() : FILE_TYPE.concat(StringUtils.SPACE).concat(extension);
        }
    }

    public void setRoots(File[] roots) {
        this.roots = roots;
    }

    public File[] getRoots() {
        return roots;
    }

    public boolean isRoot(File file) {
        for (File root : roots) {
            if (root.equals(file)) {
                return true;
            }
        }
        return false;
    }

    public String getExtension(File file) {
        return file.isDirectory() ? FOLDER_TYPE : getExtension(file.getName());
    }

    private String getExtension(String fileName) {
        int extensionDot = fileName.lastIndexOf(EXTENSION_DOT_CHAR);
        return extensionDot == NO_EXTENSION_DOT ? StringUtils.EMPTY : fileName.substring(extensionDot);
    }
}