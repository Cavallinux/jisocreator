package cl.cavallinux.jisocreator.model.isoexplorer.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;

@DisplayName("IsoTreeNode tests")
class IsoTreeNodeTest {

    // -------------------------------------------------------------------------
    // addLeafNode — nodo simple (archivo)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("addLeafNode should add a file node to parent children")
    void addLeafNodeShouldAddFileNodeToParentChildren(@TempDir Path tempDir) throws IOException {
        File file = Files.createFile(tempDir.resolve("readme.txt")).toFile();
        IsoTreeNode root = new IsoTreeNode();
        IsoTreeNode child = new IsoTreeNode(root, file);

        root.addLeafNode(child);

        assertEquals(1, root.getChildren().size());
        assertSame(child, root.getChildren().get(0));
    }

    @Test
    @DisplayName("addLeafNode should not recurse into directory children")
    void addLeafNodeShouldNotRecurseIntoDirectoryChildren(@TempDir Path tempDir) throws IOException {
        Path subDir = Files.createDirectory(tempDir.resolve("docs"));
        Files.createFile(subDir.resolve("file1.txt"));
        Files.createFile(subDir.resolve("file2.txt"));

        IsoTreeNode root = new IsoTreeNode();
        IsoTreeNode dirNode = new IsoTreeNode(root, subDir.toFile());

        root.addLeafNode(dirNode);

        // The root should have exactly one child (the directory node)
        // and the directory node itself should have NO children
        // (because addLeafNode does not recurse)
        assertEquals(1, root.getChildren().size());
        assertFalse(dirNode.hasChildren(),
                "addLeafNode must NOT auto-populate directory children");
    }

    @Test
    @DisplayName("addLeafNode should not add duplicate nodes")
    void addLeafNodeShouldNotAddDuplicateNodes(@TempDir Path tempDir) throws IOException {
        File file = Files.createFile(tempDir.resolve("dup.txt")).toFile();
        IsoTreeNode root = new IsoTreeNode();
        IsoTreeNode child = new IsoTreeNode(root, file);

        root.addLeafNode(child);
        root.addLeafNode(child); // duplicate call

        assertEquals(1, root.getChildren().size(),
                "Duplicate node must not be added twice");
    }

    @Test
    @DisplayName("addLeafNode should add multiple different nodes")
    void addLeafNodeShouldAddMultipleDifferentNodes(@TempDir Path tempDir) throws IOException {
        File fileA = Files.createFile(tempDir.resolve("a.txt")).toFile();
        File fileB = Files.createFile(tempDir.resolve("b.txt")).toFile();
        IsoTreeNode root = new IsoTreeNode();
        IsoTreeNode nodeA = new IsoTreeNode(root, fileA);
        IsoTreeNode nodeB = new IsoTreeNode(root, fileB);

        root.addLeafNode(nodeA);
        root.addLeafNode(nodeB);

        assertEquals(2, root.getChildren().size());
        assertSame(nodeA, root.getChildren().get(0));
        assertSame(nodeB, root.getChildren().get(1));
    }

    // -------------------------------------------------------------------------
    // addNode — comportamiento original (con recursion automatica)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("addNode should auto-recurse into directory children")
    void addNodeShouldAutoRecurseIntoDirectoryChildren(@TempDir Path tempDir) throws IOException {
        Path subDir = Files.createDirectory(tempDir.resolve("src"));
        Files.createFile(subDir.resolve("Main.java"));
        Files.createFile(subDir.resolve("Util.java"));

        IsoTreeNode root = new IsoTreeNode();
        IsoTreeNode dirNode = new IsoTreeNode(root, subDir.toFile());

        root.addNode(dirNode);

        // addNode must have auto-populated children of dirNode
        assertEquals(1, root.getChildren().size());
        assertTrue(dirNode.hasChildren(),
                "addNode must auto-populate directory children");
        assertEquals(2, dirNode.getChildren().size());
    }

    @Test
    @DisplayName("addNode should not add duplicate nodes")
    void addNodeShouldNotAddDuplicateNodes(@TempDir Path tempDir) throws IOException {
        File file = Files.createFile(tempDir.resolve("only.txt")).toFile();
        IsoTreeNode root = new IsoTreeNode();
        IsoTreeNode child = new IsoTreeNode(root, file);

        root.addNode(child);
        root.addNode(child);

        assertEquals(1, root.getChildren().size());
    }

    // -------------------------------------------------------------------------
    // Contraste entre addNode y addLeafNode para directorios
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("addLeafNode vs addNode: only addNode should populate directory children")
    void addLeafNodeVsAddNodeForDirectory(@TempDir Path tempDir) throws IOException {
        Path dir = Files.createDirectory(tempDir.resolve("data"));
        Files.createFile(dir.resolve("item.csv"));

        IsoTreeNode rootForLeaf = new IsoTreeNode();
        IsoTreeNode dirNodeForLeaf = new IsoTreeNode(rootForLeaf, dir.toFile());
        rootForLeaf.addLeafNode(dirNodeForLeaf);

        IsoTreeNode rootForFull = new IsoTreeNode();
        IsoTreeNode dirNodeForFull = new IsoTreeNode(rootForFull, dir.toFile());
        rootForFull.addNode(dirNodeForFull);

        assertFalse(dirNodeForLeaf.hasChildren(),
                "addLeafNode: directory node must have no auto-populated children");
        assertTrue(dirNodeForFull.hasChildren(),
                "addNode: directory node must have auto-populated children");
    }

    // -------------------------------------------------------------------------
    // ITreeNode default addLeafNode (interface)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("ITreeNode default addLeafNode should be a no-op")
    void iTreeNodeDefaultAddLeafNodeShouldBeNoOp() {
        ITreeNode node = new ITreeNode() {};
        // Must not throw and must produce no visible state change
        node.addLeafNode(new ITreeNode() {});
        assertFalse(node.hasChildren());
    }
}
