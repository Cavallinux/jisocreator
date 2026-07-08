package cl.cavallinux.jisocreator.model.osexplorer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.program.Program;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class OSExplorer {
    private File[] roots;
    private static final String FOLDER_TYPE = "Folder";
    private static final String FILE_TYPE = "File";
    private static final int NO_EXTENSION_DOT = -1;
    private static final char EXTENSION_DOT_CHAR = '.';
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    private OSExplorer(File[] roots) {
        log.info("OS: {}, Legacy FileSystem roots: {}", System.getProperty("os.name"), roots);
        this.setRoots(roots);
    }

    @Builder
    public OSExplorer() {
        this(File.listRoots());
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
            FileTime lastModifiedTime = Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS);
            Instant lastModifiedInstant = lastModifiedTime.toInstant();
            return FORMATTER.format(lastModifiedInstant);
            // return DateFormat.getDateTimeInstance().format(new
            // Date(lastModifiedTime.toMillis()));
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
        File file = path.toFile();
        return Arrays.asList(getRoots()).stream().anyMatch(root -> root.compareTo(file) == 0);
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