package cl.cavallinux.jisocreator.model.parser.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cl.cavallinux.jisocreator.model.isoexplorer.decl.ITreeNode;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoTreeNode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
final class XMLIsoFilesystemContractMapper {
    private static final String ENTRY_CLASS = "entry";
    private static final String PARENT_REFERENCE = "../../..";

    static XMLIsoFilesystemContract.Iso9660Document toDocument(IsoFileSystem isoFilesystem) {
        XMLIsoFilesystemContract.Iso9660Document document = new XMLIsoFilesystemContract.Iso9660Document();
        document.setVolumeID(isoFilesystem.getVolumeID());
        document.setApplicationID(isoFilesystem.getApplicationID());
        document.setPublisherID(isoFilesystem.getPublisherID());
        document.setIsoLength(isoFilesystem.getIsoLength());
        document.setRootEntry(toEntry(isoFilesystem.getRoot()));
        return document;
    }

    static IsoFileSystem toIsoFilesystem(XMLIsoFilesystemContract.Iso9660Document document) {
        if (Objects.isNull(document) || Objects.isNull(document.getRootEntry())) {
            return null;
        }

        IsoFileSystem isoFilesystem = new IsoFileSystem();
        isoFilesystem.setVolumeID(document.getVolumeID());
        isoFilesystem.setApplicationID(document.getApplicationID());
        isoFilesystem.setPublisherID(document.getPublisherID());
        isoFilesystem.setIsoLength(document.getIsoLength());
        isoFilesystem.setIsoPaths(null);
        isoFilesystem.setRoot(toNode(document.getRootEntry(), null));
        return isoFilesystem;
    }

    private static XMLIsoFilesystemContract.Entry toEntry(ITreeNode node) {
        XMLIsoFilesystemContract.Entry entry = new XMLIsoFilesystemContract.Entry();
        entry.setRoot(node.isRoot());
        entry.setIsoName(node.getIsoName());
        if (node.isRoot()) {
            entry.setEntryClass(ENTRY_CLASS);
        } else {
            Object element = node.getElement();
            if (element instanceof File file) {
                /**
                 * Use getPath() and normalize path separators to forward slashes
                 * for cross-platform compatibility (e.g., ISO layouts created on Linux
                 * opened on Windows should preserve original Unix-style paths on round-trip)
                 */
                entry.setFile(file.getPath().replace(File.separatorChar, '/'));
            }
            entry.setParent(defaultParentRef());
        }

        List<XMLIsoFilesystemContract.Entry> children = new ArrayList<>();
        for (ITreeNode child : node.getChildren()) {
            children.add(toEntry(child));
        }
        entry.setChildren(children);

        return entry;
    }

    private static ITreeNode toNode(XMLIsoFilesystemContract.Entry entry, ITreeNode parent) {
        File file = entry.isRoot() || entry.getFile() == null ? null : new File(entry.getFile());
        IsoTreeNode node = new IsoTreeNode(parent, file, entry.getIsoName(), entry.isRoot());

        List<ITreeNode> children = new ArrayList<>();
        if (entry.getChildren() != null) {
            for (XMLIsoFilesystemContract.Entry childEntry : entry.getChildren()) {
                children.add(toNode(childEntry, node));
            }
        }
        node.setChildren(children);
        return node;
    }

    private static XMLIsoFilesystemContract.ParentRef defaultParentRef() {
        XMLIsoFilesystemContract.ParentRef parentRef = new XMLIsoFilesystemContract.ParentRef();
        parentRef.setEntryClass(ENTRY_CLASS);
        parentRef.setReference(PARENT_REFERENCE);
        return parentRef;
    }
}
