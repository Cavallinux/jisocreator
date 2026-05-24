package cl.cavallinux.jisocreator.model.osexplorer;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class OSExplorer {
    private File[] roots;
    private Path[] rootPaths;
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

    private OSExplorer(File[] roots, Path[] rootsPath) {
        log.info("OS: {}, Legacy FileSystem roots: {}", System.getProperty("os.name"), roots);
        log.info("Java NIO FileSystem roots: {}", (Object[]) rootsPath);
        this.setRoots(roots);
        this.setRootPaths(rootsPath);
    }

    private OSExplorer() {
        this(loadLegacyOSRoots(), loadOSRoots());
    }

    private static OSExplorer newInstance() {
        return new OSExplorer();
    }

    @Deprecated(since = "0.2.0", forRemoval = true)
    private static File[] loadLegacyOSRoots() {
        return Strings.CI.equalsAny(SWT.getPlatform(), "gtk") ? File.listRoots()
                : new File(System.getProperty("user.home")).listFiles();
    }

    private static Path[] loadOSRoots() {
        try {
            return Strings.CI.equalsAny(SWT.getPlatform(), "gtk") ? StreamSupport
                    .stream(FileSystems.getDefault().getRootDirectories().spliterator(), false).toArray(Path[]::new)
                    : Files.list(Paths.get(System.getProperty("user.home"))).toArray(Path[]::new);
        } catch (IOException e) {
            log.error("Error loading roots", e);
            return new Path[0];
        }
    }

    /**
     * Launches the default application associated with the specified file path.
     * This method utilizes the modern `Path` API, which is more efficient and
     * compatible with modern Java versions compared to the legacy `File` API. It is
     * recommended to use this method for launching files, as it provides better
     * performance and compatibility.
     * 
     * @param filePath The path of the file to be launched.
     * @return true if the file was successfully launched, false otherwise.
     */
    public boolean launch(Path filePath) {
        return Program.launch(filePath.toAbsolutePath().toString());
    }

    /**
     * Retrieves the name of the specified file path. This method utilizes the
     * modern `Path` API, which is more efficient and compatible with modern Java
     * versions compared to the legacy `File` API. It is recommended to use this
     * method for retrieving file names, as it provides better performance and
     * compatibility.
     * 
     * @param path The path of the file whose name is to be retrieved.
     * @return The name of the file.
     */
    public String getName(Path path) {
        return isRoot(path) ? getAbsolutePath(path) : path.getFileName().toString();
    }

    /**
     * Retrieves the absolute path of the specified file path. This method utilizes
     * the modern `Path` API, which is more efficient and compatible with modern
     * Java versions
     * 
     * @param path The path of the file whose absolute path is to be retrieved.
     * @return The absolute path of the file as a string.
     */
    public String getAbsolutePath(Path path) {
        return path.toAbsolutePath().toString();
    }

    /**
     * Retrieves the size of the specified file path. This method utilizes the
     * modern `Path` API, which is more efficient and compatible with modern Java
     * versions compared to the legacy `File` API.
     * 
     * @param path
     * @return The size of the file in bytes as a string.
     */
    public String length(Path path) {
        try {
            return Long.toString(Files.size(path));
        } catch (IOException e) {
            log.warn("Error retrieving file size for path: {} . Calculating using java.io.File API", path, e);
            return Long.toString(path.toFile().length());
        }
    }

    /**
     * Retrieves the last modified time of the specified file path. This method
     * utilizes the modern `Path` API, which is more efficient and compatible with
     * modern Java versions compared to the legacy `File` API.
     * 
     * @param path The path of the file whose last modified time is to be retrieved.
     * @return The last modified time of the file as a formatted string. If an error
     *         occurs while retrieving the last modified time, it returns "0".
     */
    // TODO use java time api.
    public String lastModified(Path path) {
        try {
            return DateFormat.getDateTimeInstance()
                    .format(new Date(Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS).toMillis()));
        } catch (IOException e) {
            log.error("Error retrieving last modified time for path: {}", path, e);
            return DateFormat.getDateTimeInstance().format(new Date(path.toFile().lastModified()));
        }
    }

    /**
     * Retrieves the file type of the specified file path. This method utilizes the
     * modern `Path` API, which is more efficient and compatible with modern Java
     * versions compared to the legacy File API.
     * 
     * @param path The path of the file whose type is to be retrieved.
     * @return The file type as a string.
     */
    public String getFileType(Path path) {
        return Files.isDirectory(path) ? FOLDER_TYPE : getFileType2(path);
    }

    /**
     * Checks if the specified path is a root directory. This method utilizes the
     * modern `Path` API, which is more efficient and compatible with modern Java
     * versions compared to the legacy `File` API.
     * 
     * @param path The path to be checked.
     * @return true if the specified path is a root directory, false otherwise.
     */
    public boolean isRoot(Path path) {
        return (Objects.nonNull(path.getRoot()) && path.getNameCount() == 0)
                || Strings.CI.equalsAny(path.toAbsolutePath().toString(), System.getProperty("user.home"));
    }

    /**
     * Retrieves the file extension of the specified file path. This method utilizes
     * the modern `Path` API, which is more efficient and compatible with modern
     * Java versions compared to the legacy `File` API.
     * 
     * @param path The path of the file whose extension is to be retrieved.
     * @return The file extension as a string. If the path represents a directory,
     *         it returns "Folder". If the file has no extension, it returns an
     *         empty string.
     */
    public String getExtension(Path path) {
        return Files.isDirectory(path) ? FOLDER_TYPE : getExtension(path.getFileName().toString());
    }
    
    private String getFileType2(Path path) {
        String extension = getExtension(path.getFileName().toString());
        return StringUtils.isBlank(extension) ? FILE_TYPE : getFileType(Program.findProgram(extension), extension);
    }
    
    private String getFileType(Program program, String extension) {
        return Objects.nonNull(program) ? program.getName() : FILE_TYPE.concat(StringUtils.SPACE).concat(extension);
    }

    private String getExtension(String fileName) {
        int extensionDot = fileName.lastIndexOf(EXTENSION_DOT_CHAR);
        return extensionDot == NO_EXTENSION_DOT ? StringUtils.EMPTY : fileName.substring(extensionDot);
    }
}