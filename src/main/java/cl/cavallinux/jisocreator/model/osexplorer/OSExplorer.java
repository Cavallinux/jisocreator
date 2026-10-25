package cl.cavallinux.jisocreator.model.osexplorer;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    /**
     * Caches the result of {@link Program#findProgram(String)} keyed by file
     * extension. This native OS-level lookup (file association / MIME query) is
     * expensive and its result is stable for the lifetime of the application, so
     * repeated lookups for files sharing the same extension are served from this
     * cache instead of hitting the OS again for every file.
     */
    private static final ConcurrentMap<String, Optional<Program>> PROGRAM_CACHE = new ConcurrentHashMap<>();

    /**
     * Maximum number of directories whose {@link BasicFileAttributes} listings
     * are kept in {@link #attributesCache} at once. Bounds memory usage while
     * still letting the user navigate back and forth between a handful of
     * recently visited directories (e.g. parent/sibling folders) without
     * paying the cost of a fresh {@link #warmAttributesCache(Path)} scan each
     * time.
     */
    private static final int MAX_CACHED_DIRECTORIES = 5;

    /**
     * Caches a {@link BasicFileAttributes} lookup per {@link Path}, grouped by
     * the entry's parent directory, consolidating what would otherwise be up
     * to three independent filesystem stat calls ({@code Files.isDirectory},
     * {@code Files.size}, {@code Files.getLastModifiedTime}) into a single
     * {@code stat(2)}-style call per file. Populated in bulk, one directory at
     * a time, by {@link #warmAttributesCache(Path)} — typically from a
     * background thread (see {@code LoadOSDirectoryContentsTask}) — so that
     * the subsequent UI-thread rendering of a directory listing reads
     * already-resolved metadata instead of hitting the filesystem again.
     * <p>
     * Up to {@link #MAX_CACHED_DIRECTORIES} directories are retained as a
     * least-recently-used (LRU) cache: warming a new directory no longer
     * discards every other cached directory, so navigating back to a
     * recently visited folder (e.g. going up to the parent and back down, or
     * switching between sibling folders) is served from cache instead of
     * triggering another filesystem scan. Once the limit is exceeded, the
     * least recently warmed directory is evicted first. The outer map is
     * synchronized since {@link LinkedHashMap} (used here for its
     * access-ordered LRU eviction) is not thread-safe on its own, while each
     * per-directory inner map remains a {@link ConcurrentHashMap} so
     * concurrent reads/writes to entries of the currently warmed directory
     * (background writer vs. UI-thread readers) stay safe.
     */
    private final Map<Path, Map<Path, BasicFileAttributes>> attributesCache = Collections
            .synchronizedMap(new LinkedHashMap<Path, Map<Path, BasicFileAttributes>>(MAX_CACHED_DIRECTORIES + 1, 0.75f,
                    true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Path, Map<Path, BasicFileAttributes>> eldest) {
                    return size() > MAX_CACHED_DIRECTORIES;
                }
            });

    private OSExplorer(File[] roots) {
        log.info("OS: {}, FileSystem roots: {}", System.getProperty("os.name"), roots);
        this.setRoots(roots);
    }

    @Builder
    public OSExplorer() {
       this(File.listRoots());
    }
    
    public File getUnixOSRoot() {
        return roots[0];
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
        BasicFileAttributes cached = cachedAttributes(path);
        if (cached != null) {
            return Long.toString(cached.size());
        }
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
    public String lastModified(Path path) {
        return FORMATTER.format(lastModifiedInstant(path));
    }
    
    private Instant lastModifiedInstant(Path path) {
        BasicFileAttributes cached = cachedAttributes(path);
        if (cached != null) {
            return cached.lastModifiedTime().toInstant();
        }
        try {
            FileTime lastModifiedTime = Files.getLastModifiedTime(path);
            return lastModifiedTime.toInstant();
        } catch (IOException e) {
            log.warn("Error retrieving last modified time for path: {}. Calculating via Java FILE Api", path, e);
            return Instant.ofEpochMilli(path.toFile().lastModified());
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
        return isDirectory(path) ? FOLDER_TYPE : getFileType2(path);
    }

    /**
     * Checks whether the specified file path is a directory. This method utilizes
     * the {@link #attributesCache} (populated in bulk by
     * {@link #warmAttributesCache(Path)}) when available, avoiding a redundant
     * {@code Files.isDirectory(Path)} filesystem call for paths whose attributes
     * were already resolved, and falls back to a direct check otherwise.
     * 
     * @param path The path to be checked.
     * @return true if the specified path is a directory, false otherwise.
     */
    public boolean isDirectory(Path path) {
        BasicFileAttributes cached = cachedAttributes(path);
        return cached != null ? cached.isDirectory() : Files.isDirectory(path);
    }

    /**
     * Looks up the {@link BasicFileAttributes} previously cached for the
     * specified path via {@link #warmAttributesCache(Path)}, if any. The
     * lookup is scoped to the path's parent directory's entry in the
     * per-directory {@link #attributesCache} LRU map; a {@code null} return
     * means either the parent directory was never warmed, its cached entries
     * were evicted (LRU capacity exceeded) or refreshed away, or the path has
     * no parent (e.g. a filesystem root).
     *
     * @param path the path whose cached attributes should be looked up
     * @return the cached attributes, or {@code null} if not present in the cache
     */
    private BasicFileAttributes cachedAttributes(Path path) {
        Path parent = path.getParent();
        if (parent == null) {
            return null;
        }
        Map<Path, BasicFileAttributes> directoryEntries = attributesCache.get(parent);
        return directoryEntries != null ? directoryEntries.get(path) : null;
    }

    /**
     * Pre-fetches and caches {@link BasicFileAttributes} for every direct entry of
     * the specified directory, consolidating the separate {@code isDirectory}/
     * {@code size}/{@code lastModifiedTime} stat calls each entry would otherwise
     * require into a single stat call per entry. Intended to be invoked from a
     * background thread (see {@code LoadOSDirectoryContentsTask}) before the
     * directory is displayed, so that the subsequent UI-thread rendering reads
     * already-resolved metadata instead of hitting the filesystem again.
     * <p>
     * Unlike a single-directory cache, warming a directory here does not discard
     * entries cached for other directories: it stores this directory's freshly
     * read entries under its own key in the {@link #attributesCache} LRU map,
     * evicting only the least-recently-warmed directory once
     * {@link #MAX_CACHED_DIRECTORIES} is exceeded. Re-warming an already cached
     * directory (e.g. the user navigates back to it) replaces its stale entries
     * with fresh ones and marks it as most-recently-used again.
     * </p>
     * 
     * @param directory the directory whose entries' attributes should be pre-fetched
     */
    public void warmAttributesCache(Path directory) {
        Map<Path, BasicFileAttributes> freshEntries = new ConcurrentHashMap<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                try {
                    freshEntries.put(entry, Files.readAttributes(entry, BasicFileAttributes.class));
                } catch (IOException e) {
                    log.warn("Error pre-fetching attributes for path: {}", entry, e);
                }
            }
        } catch (IOException e) {
            log.warn("Error warming attributes cache for directory: {}", directory, e);
        }
        attributesCache.put(directory, freshEntries);
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
        return isDirectory(path) ? FOLDER_TYPE : getExtension(path.getFileName().toString());
    }

    private String getFileType2(Path path) {
        String extension = getExtension(path.getFileName().toString());
        return StringUtils.isBlank(extension) ? FILE_TYPE : getFileType(findProgram(extension), extension);
    }

    /**
     * Resolves the {@link Program} associated with the specified file extension,
     * caching the result. This method delegates to {@link Program#findProgram(String)},
     * a potentially expensive OS-level lookup (e.g. MIME/file-association queries),
     * but avoids repeating that lookup for extensions already resolved during the
     * current application session.
     *
     * @param extension the file extension to resolve (including the leading dot)
     * @return the associated {@link Program}, or {@code null} if none is registered
     */
    public Program findProgram(String extension) {
        return PROGRAM_CACHE.computeIfAbsent(extension, ext -> Optional.ofNullable(Program.findProgram(ext)))
                .orElse(null);
    }

    private String getFileType(Program program, String extension) {
        return Objects.nonNull(program) ? program.getName() : FILE_TYPE.concat(StringUtils.SPACE).concat(extension);
    }

    private String getExtension(String fileName) {
        int extensionDot = fileName.lastIndexOf(EXTENSION_DOT_CHAR);
        return extensionDot == NO_EXTENSION_DOT ? StringUtils.EMPTY : fileName.substring(extensionDot);
    }
}