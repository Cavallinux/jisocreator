package cl.cavallinux.jisocreator.model.parser;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

final class XMLIsoFilesystemContract {
    private XMLIsoFilesystemContract() {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JacksonXmlRootElement(localName = "iso9660")
    static class Iso9660Document {
        @JacksonXmlProperty(isAttribute = true, localName = "volumeid")
        private String volumeID;
        @JacksonXmlProperty(isAttribute = true, localName = "applicationid")
        private String applicationID;
        @JacksonXmlProperty(isAttribute = true, localName = "publisherid")
        private String publisherID;
        @JacksonXmlProperty(isAttribute = true, localName = "isolength")
        private Long isoLength;
        @JacksonXmlProperty(localName = "RootEntry")
        private Entry rootEntry;

        public String getVolumeID() {
            return volumeID;
        }

        public void setVolumeID(String volumeID) {
            this.volumeID = volumeID;
        }

        public String getApplicationID() {
            return applicationID;
        }

        public void setApplicationID(String applicationID) {
            this.applicationID = applicationID;
        }

        public String getPublisherID() {
            return publisherID;
        }

        public void setPublisherID(String publisherID) {
            this.publisherID = publisherID;
        }

        public Long getIsoLength() {
            return isoLength;
        }

        public void setIsoLength(Long isoLength) {
            this.isoLength = isoLength;
        }

        public Entry getRootEntry() {
            return rootEntry;
        }

        public void setRootEntry(Entry rootEntry) {
            this.rootEntry = rootEntry;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Entry {
        @JacksonXmlProperty(isAttribute = true, localName = "class")
        private String entryClass;
        @JacksonXmlProperty(isAttribute = true, localName = "file")
        private String file;
        @JacksonXmlProperty(isAttribute = true, localName = "isoname")
        private String isoName;
        @JacksonXmlProperty(isAttribute = true, localName = "root")
        private boolean root;
        @JacksonXmlProperty(localName = "parent")
        private ParentRef parent;
        @JacksonXmlElementWrapper(localName = "children")
        @JacksonXmlProperty(localName = "entry")
        private List<Entry> children = new ArrayList<>();

        public String getEntryClass() {
            return entryClass;
        }

        public void setEntryClass(String entryClass) {
            this.entryClass = entryClass;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getIsoName() {
            return isoName;
        }

        public void setIsoName(String isoName) {
            this.isoName = isoName;
        }

        public boolean isRoot() {
            return root;
        }

        public void setRoot(boolean root) {
            this.root = root;
        }

        public ParentRef getParent() {
            return parent;
        }

        public void setParent(ParentRef parent) {
            this.parent = parent;
        }

        public List<Entry> getChildren() {
            return children;
        }

        public void setChildren(List<Entry> children) {
            this.children = children;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ParentRef {
        @JacksonXmlProperty(isAttribute = true, localName = "class")
        private String entryClass;
        @JacksonXmlProperty(isAttribute = true, localName = "reference")
        private String reference;

        public String getEntryClass() {
            return entryClass;
        }

        public void setEntryClass(String entryClass) {
            this.entryClass = entryClass;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }
    }
}
