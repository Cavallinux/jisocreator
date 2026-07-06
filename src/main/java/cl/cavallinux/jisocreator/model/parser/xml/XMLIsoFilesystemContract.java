package cl.cavallinux.jisocreator.model.parser.xml;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
final class XMLIsoFilesystemContract {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JacksonXmlRootElement(localName = "iso9660")
    @Getter
    @Setter
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
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
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
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    static class ParentRef {
        @JacksonXmlProperty(isAttribute = true, localName = "class")
        private String entryClass;
        @JacksonXmlProperty(isAttribute = true, localName = "reference")
        private String reference;
    }
}
