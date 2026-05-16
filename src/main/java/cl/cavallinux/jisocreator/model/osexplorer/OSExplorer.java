package cl.cavallinux.jisocreator.model.osexplorer;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
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
            log.error("Error retrieving file size for path: {}", path, e);
            return BigInteger.ZERO.toString();
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
     * Checks if the specified path is a root directory. This method utilizes the
     * modern `Path` API, which is more efficient and compatible with modern Java
     * versions compared to the legacy `File` API.
     * 
     * @param path The path to be checked.
     * @return true if the specified path is a root directory, false otherwise.
     */
    public boolean isRoot(Path path) {
        return Objects.nonNull(path.getRoot()) && path.getNameCount() == 0;
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

    private String getExtension(String fileName) {
        int extensionDot = fileName.lastIndexOf(EXTENSION_DOT_CHAR);
        return extensionDot == NO_EXTENSION_DOT ? StringUtils.EMPTY : fileName.substring(extensionDot);
    }
}