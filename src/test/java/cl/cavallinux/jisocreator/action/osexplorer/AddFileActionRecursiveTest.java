package cl.cavallinux.jisocreator.action.osexplorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoTreeNode;

@DisplayName("AddFileAction.addFileRecursively tests")
class AddFileActionRecursiveTest {

    private AddFileAction action;
    private IProgressMonitor monitor;
    private Method addFileRecursively;

    @BeforeEach
    void setUp() throws Exception {
        action = AddFileAction.builder().build();
        monitor = mock(IProgressMonitor.class);
        addFileRecursively = AddFileAction.class.getDeclaredMethod(
                "addFileRecursively", ITreeNode.class, File.class, IProgressMonitor.class);
        addFileRecursively.setAccessible(true);
    }

    /**
     * -------------------------------------------------------------------------
     * Archivo simple — sin cancelacion
     * -------------------------------------------------------------------------
     */

    @Test
    @DisplayName("Should add a single file node to parent and report worked")
    void shouldAddSingleFileNodeToParentAndReportWorked(@TempDir Path tempDir) throws Exception {
        File file = Files.createFile(tempDir.resolve("data.txt")).toFile();
        IsoTreeNode root = new IsoTreeNode();
        when(monitor.isCanceled()).thenReturn(false);

        invoke(root, file);

        assertEquals(1, root.getChildren().size(),
                "Parent must have exactly one child after adding a file");
        ITreeNode added = root.getChildren().get(0);
        assertEquals(file, added.getElement());
        verify(monitor, atLeastOnce()).worked(1);
    }

    @Test
    @DisplayName("Should set subTask with file path before adding node")
    void shouldSetSubTaskWithFilePathBeforeAddingNode(@TempDir Path tempDir) throws Exception {
        File file = Files.createFile(tempDir.resolve("report.csv")).toFile();
        IsoTreeNode root = new IsoTreeNode();
        when(monitor.isCanceled()).thenReturn(false);

        invoke(root, file);

        verify(monitor).subTask("Adding: " + file.getAbsolutePath());
    }

    /**
     * -------------------------------------------------------------------------
     * Cancelacion inmediata
     * -------------------------------------------------------------------------
     */

    @Test
    @DisplayName("Should throw InterruptedException immediately when monitor is already cancelled")
    void shouldThrowInterruptedExceptionWhenMonitorAlreadyCancelled(@TempDir Path tempDir) throws IOException {
        File file = Files.createFile(tempDir.resolve("ignored.txt")).toFile();
        IsoTreeNode root = new IsoTreeNode();
        when(monitor.isCanceled()).thenReturn(true);

        InvocationTargetException ex = assertThrows(InvocationTargetException.class,
                () -> invoke(root, file));

        assertTrue(ex.getCause() instanceof InterruptedException,
                "Cause must be InterruptedException");
        assertEquals(0, root.getChildren().size(),
                "No node must be added when cancelled before processing");
        verify(monitor, never()).worked(1);
    }

    /**
     * -------------------------------------------------------------------------
     * Directorio con hijos — sin cancelacion
     * -------------------------------------------------------------------------
     */

    @Test
    @DisplayName("Should recursively add all files inside a directory")
    void shouldRecursivelyAddAllFilesInsideDirectory(@TempDir Path tempDir) throws Exception {
        Path subDir = Files.createDirectory(tempDir.resolve("assets"));
        Files.createFile(subDir.resolve("icon.png"));
        Files.createFile(subDir.resolve("logo.svg"));

        IsoTreeNode root = new IsoTreeNode();
        when(monitor.isCanceled()).thenReturn(false);

        invoke(root, subDir.toFile());

        /** Root should have the directory node */
        assertEquals(1, root.getChildren().size());
        ITreeNode dirNode = root.getChildren().get(0);
        assertTrue(dirNode.hasChildren(),
                "Directory node must have children after recursive add");
        assertEquals(2, dirNode.getChildren().size());
    }

    @Test
    @DisplayName("Should recursively add nested subdirectories")
    void shouldRecursivelyAddNestedSubdirectories(@TempDir Path tempDir) throws Exception {
        Path level1 = Files.createDirectory(tempDir.resolve("level1"));
        Path level2 = Files.createDirectory(level1.resolve("level2"));
        Files.createFile(level2.resolve("deep.txt"));

        IsoTreeNode root = new IsoTreeNode();
        when(monitor.isCanceled()).thenReturn(false);

        invoke(root, level1.toFile());

        ITreeNode level1Node = root.getChildren().get(0);
        assertEquals(1, level1Node.getChildren().size(),
                "level1 must contain level2 directory");
        ITreeNode level2Node = level1Node.getChildren().get(0);
        assertEquals(1, level2Node.getChildren().size(),
                "level2 must contain the deep file");
    }

    /**
     * -------------------------------------------------------------------------
     * Directorio vacio
     * -------------------------------------------------------------------------
     */

    @Test
    @DisplayName("Should add empty directory node without children")
    void shouldAddEmptyDirectoryNodeWithoutChildren(@TempDir Path tempDir) throws Exception {
        Path emptyDir = Files.createDirectory(tempDir.resolve("empty"));
        IsoTreeNode root = new IsoTreeNode();
        when(monitor.isCanceled()).thenReturn(false);

        invoke(root, emptyDir.toFile());

        assertEquals(1, root.getChildren().size());
        assertFalse(root.getChildren().get(0).hasChildren(),
                "Empty directory node must have no children");
    }

    /**
     * -------------------------------------------------------------------------
     * Cancelacion a mitad de la recursion
     * -------------------------------------------------------------------------
     */

    @Test
    @DisplayName("Should stop recursion and throw InterruptedException when cancelled inside directory")
    void shouldStopRecursionWhenCancelledInsideDirectory(@TempDir Path tempDir) throws IOException {
        Path subDir = Files.createDirectory(tempDir.resolve("large"));
        Files.createFile(subDir.resolve("first.dat"));
        Files.createFile(subDir.resolve("second.dat"));
        Files.createFile(subDir.resolve("third.dat"));

        IsoTreeNode root = new IsoTreeNode();

        /** Not cancelled for the directory itself, but cancelled on the first child */
        when(monitor.isCanceled())
                /** directory node — not cancelled */
                .thenReturn(false)
                /** first child — cancelled */
                .thenReturn(true);

        InvocationTargetException ex = assertThrows(InvocationTargetException.class,
                () -> invoke(root, subDir.toFile()));

        assertTrue(ex.getCause() instanceof InterruptedException,
                "Cause must be InterruptedException when cancelled mid-recursion");

        /** Directory node was added to root before the cancellation inside recursion */
        assertEquals(1, root.getChildren().size(),
                "The directory node itself must have been added before cancellation");

        ITreeNode dirNode = root.getChildren().get(0);
        /** Fewer than 3 children should have been added (stopped early) */
        assertTrue(dirNode.getChildren().size() < 3,
                "Recursion must have stopped before all children were added");
    }

    /**
     * -------------------------------------------------------------------------
     * run(IProgressMonitor) - lista de archivos via reflection en campos privados
     * -------------------------------------------------------------------------
     */

    @Test
    @DisplayName("run(IProgressMonitor) should process all files in the list and call done()")
    void runMonitorShouldProcessAllFilesAndCallDone(@TempDir Path tempDir) throws Exception {
        File fileA = Files.createFile(tempDir.resolve("alpha.txt")).toFile();
        File fileB = Files.createFile(tempDir.resolve("beta.txt")).toFile();

        IsoTreeNode root = new IsoTreeNode();
        when(monitor.isCanceled()).thenReturn(false);

        /** Inject private fields via reflection */
        setPrivateField(action, "files", java.util.List.of(fileA, fileB));
        setPrivateField(action, "isoNode", root);

        action.run(monitor);

        assertEquals(2, root.getChildren().size(),
                "Both files should have been added as leaf nodes");
        verify(monitor).done();
    }

    @Test
    @DisplayName("run(IProgressMonitor) should throw InterruptedException when cancelled before first file")
    void runMonitorShouldThrowInterruptedExceptionWhenCancelledBeforeFirstFile(@TempDir Path tempDir)
            throws Exception {
        File file = Files.createFile(tempDir.resolve("skipped.dat")).toFile();

        IsoTreeNode root = new IsoTreeNode();
        when(monitor.isCanceled()).thenReturn(true);

        setPrivateField(action, "files", java.util.List.of(file));
        setPrivateField(action, "isoNode", root);

        org.junit.jupiter.api.Assertions.assertThrows(InterruptedException.class,
                () -> action.run(monitor));

        assertEquals(0, root.getChildren().size(),
                "No child must be added when cancelled immediately");
    }

    /**
     * -------------------------------------------------------------------------
     * Builder
     * -------------------------------------------------------------------------
     */

    @Test
    @DisplayName("Builder should produce a non-null AddFileAction instance")
    void builderShouldProduceNonNullInstance() {
        AddFileAction built = AddFileAction.builder().build();
        org.junit.jupiter.api.Assertions.assertNotNull(built);
    }

    /**
     * -------------------------------------------------------------------------
     * Helpers
     * -------------------------------------------------------------------------
     */

    private void invoke(ITreeNode parent, File file) throws Exception {
        addFileRecursively.invoke(action, parent, file, monitor);
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }
}
