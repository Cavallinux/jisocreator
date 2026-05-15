package cl.cavallinux.jisocreator.model.osexplorer;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
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

    private OSExplorer(File[] roots, Iterable<Path> rootsPath) {
        log.info("OS: {}, Legacy FileSystem roots: {}", System.getProperty("os.name"), roots);
        log.info("Java NIO FileSystem roots: {}", rootsPath);
        this.setRoots(roots);
    }

    private OSExplorer() {
        this(File.listRoots(), FileSystems.getDefault().getRootDirectories());
    }

    private static OSExplorer newInstance() {
        return new OSExplorer();
    }

    /**
     * Launches the default application associated with the specified file. This
     * method is deprecated because it relies on the legacy `File` API, which may
     * not be as efficient or compatible with modern Java versions. It is
     * recommended to use the `launch(Path)` method instead, which utilizes the
     * newer `Path` API for better performance and compatibility.
     * 
     * @param file
     * @return
     * @deprecated Use {@link #launch(Path)} instead, as it is more efficient and
     *             compatible with modern Java versions.
     */
    @Deprecated
    public boolean launch(File file) {
        return Program.launch(file.getAbsolutePath());
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
     * Retrieves the name of the specified file. This method is deprecated because
     * it relies on the legacy `File` API, which may not be as efficient or
     * compatible with modern Java versions.
     * 
     * @param file
     * @return
     * @deprecated Use {@link #getName(Path)} instead, as it is more efficient and
     *             compatible with modern Java versions.
     */
    @Deprecated
    public String getName(File file) {
        return file.getName();
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
        boolean isRoot = path.getRoot() != null && path.getNameCount() == 0;
        return isRoot ? getAbsolutePath(path) : path.getFileName().toString();
    }

    /**
     * Retrieves the absolute path of the specified file. This method is deprecated
     * because it relies on the legacy `File` API, which may not be as efficient or
     * compatible with modern Java versions.
     * 
     * @param file
     * @return
     * @deprecated Use {@link #getAbsolutePath(Path)} instead, as it is more
     *             efficient and compatible with modern Java versions.
     */
    @Deprecated
    public String getAbsolutePath(File file) {
        return file.getAbsolutePath();
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
     * Retrieves the size of the specified file. This method is deprecated because
     * it relies on the legacy `File` API. The `File` API's `length()` method may
     * not be as efficient or compatible with modern Java versions.
     * 
     * @param file The file whose size is to be retrieved.
     * @return The size of the file in bytes as a string.
     */
    @Deprecated
    public String length(File file) {
        return Long.toString(file.length());
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
            log.error("Error retrieving file size for path: {}", path, e);
            return BigInteger.ZERO.toString();
        }
    }

    /**
     * Retrieves the last modified time of the specified file. This method is
     * deprecated because it relies on the legacy `File` API, which may not be as
     * efficient or compatible with modern Java versions.
     * 
     * @param file The file whose last modified time is to be retrieved.
     * @return The last modified time of the file as a formatted string.
     */
    @Deprecated
    public String lastModified(File file) {
        return DateFormat.getDateTimeInstance().format(new Date(file.lastModified()));
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
    //TODO use java time api.
    public String lastModified(Path path) {
        try {
            return DateFormat.getDateTimeInstance()
                    .format(new Date(Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS).toMillis()));
        } catch (IOException e) {
            log.error("Error retrieving last modified time for path: {}", path, e);
            return BigInteger.ZERO.toString();
        }
    }

    /**
     * Retrieves the file type of the specified file. This method is deprecated
     * because it relies on the legacy `File` API, which may not be as efficient or
     * compatible with modern Java versions. It is recommended to use the
     * `getFileType(Path)` method instead, which utilizes the newer `Path` API for
     * better performance and compatibility.
     * 
     * @param file The file whose type is to be retrieved.
     * @return The file type as a string.
     * @deprecated Use {@link #getFileType(Path)} instead, as it is more efficient
     *             and compatible with modern Java versions.
     */
    @Deprecated
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

    /**
     * Retrieves the file type of the specified file path. This method utilizes the
     * modern `Path` API, which is more efficient and compatible with modern Java
     * versions compared to the legacy File API.
     * 
     * @param path The path of the file whose type is to be retrieved.
     * @return The file type as a string.
     */
    public String getFileType(Path path) {
        if (Files.isDirectory(path)) {
            return FOLDER_TYPE;
        } else {
            String extension = getExtension(path.getFileName().toString());
            if (StringUtils.isBlank(extension)) {
                return FILE_TYPE;
            } else {
                Program program = Program.findProgram(extension);
                return Objects.nonNull(program) ? program.getName()
                        : FILE_TYPE.concat(StringUtils.SPACE).concat(extension);
            }
        }
    }

    /**
     * Checks if the specified file is a root directory. This method is deprecated
     * because it relies on the legacy `File` API, which may not be as efficient or
     * compatible with modern Java versions. It is recommended to use the
     * `isRoot(Path)` method instead, which utilizes the newer `Path` API for better
     * performance and compatibility.
     * 
     * @param file The file to be checked.
     * @return true if the specified file is a root directory, false otherwise.
     * @deprecated Use {@link #isRoot(Path)} instead, as it is more efficient and
     *             compatible with modern Java versions.
     */
    @Deprecated
    public boolean isRoot(File file) {
        for (File root : roots) {
            if (root.equals(file)) {
                return true;
            }
        }
        return false;
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
        for (Path root : rootPaths) {
            if (root.equals(path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the file extension of the specified file. This method is deprecated
     * because it relies on the legacy `File` API, which may not be as efficient or
     * compatible with modern Java versions. It is recommended to use the
     * `getExtension(Path)` method instead, which utilizes the newer `Path` API for
     * better performance and compatibility.
     * 
     * @param file The file whose extension is to be retrieved.
     * @return The file extension as a string. If the file is a directory, it
     *         returns "Folder". If the file has no extension, it returns an empty
     *         string.
     * @deprecated Method will be removed in future versions of JIsocreator.
     */
    @Deprecated
    public String getExtension(File file) {
        return file.isDirectory() ? FOLDER_TYPE : getExtension(file.getName());
    }

    private String getExtension(String fileName) {
        int extensionDot = fileName.lastIndexOf(EXTENSION_DOT_CHAR);
        return extensionDot == NO_EXTENSION_DOT ? StringUtils.EMPTY : fileName.substring(extensionDot);
    }
}