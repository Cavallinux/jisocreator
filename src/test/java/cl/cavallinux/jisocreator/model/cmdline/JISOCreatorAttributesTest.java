package cl.cavallinux.jisocreator.model.cmdline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("JISOCreatorAttributes tests")
class JISOCreatorAttributesTest {

    @Test
    @DisplayName("Should build a record exposing all supplied attribute values")
    void shouldBuildRecordExposingAllSuppliedValues() {
        JISOCreatorAttributes attributes = JISOCreatorAttributes.builder()
                .appName("JISOCreator")
                .appVersion("0.2.1-SNAPSHOT")
                .jvmVersion("21")
                .jvmVendor("Eclipse Adoptium")
                .osName("Linux")
                .build();

        assertEquals("JISOCreator", attributes.appName());
        assertEquals("0.2.1-SNAPSHOT", attributes.appVersion());
        assertEquals("21", attributes.jvmVersion());
        assertEquals("Eclipse Adoptium", attributes.jvmVendor());
        assertEquals("Linux", attributes.osName());
    }

    @Test
    @DisplayName("Should support toBuilder to derive a modified copy")
    void shouldSupportToBuilderToDeriveModifiedCopy() {
        JISOCreatorAttributes original = JISOCreatorAttributes.builder()
                .appName("JISOCreator")
                .appVersion("0.2.1-SNAPSHOT")
                .jvmVersion("21")
                .jvmVendor("Eclipse Adoptium")
                .osName("Linux")
                .build();

        JISOCreatorAttributes copy = original.toBuilder().osName("Windows 11").build();

        assertEquals("Linux", original.osName());
        assertEquals("Windows 11", copy.osName());
        assertEquals(original.appName(), copy.appName());
        assertNotEquals(original, copy);
    }

    @Test
    @DisplayName("Should implement value-based equals and hashCode")
    void shouldImplementValueBasedEqualsAndHashCode() {
        JISOCreatorAttributes first = JISOCreatorAttributes.builder().appName("A").appVersion("1").jvmVersion("21")
                .jvmVendor("V").osName("OS").build();
        JISOCreatorAttributes second = JISOCreatorAttributes.builder().appName("A").appVersion("1").jvmVersion("21")
                .jvmVendor("V").osName("OS").build();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    @DisplayName("Should build attributes from ICommandLineParser.buildAttributes reflecting current JVM/OS")
    void shouldBuildAttributesFromCommandLineParserHelper() {
        JISOCreatorAttributes attributes = ICommandLineParser.buildAttributes();

        assertNotNull(attributes);
        assertEquals(System.getProperty("java.version"), attributes.jvmVersion());
        assertEquals(System.getProperty("java.specification.vendor"), attributes.jvmVendor());
        assertEquals(System.getProperty("os.name"), attributes.osName());
    }

    @Test
    @DisplayName("Should format default toString as \"appName version appVersion\"")
    void shouldFormatDefaultToStringAsNameAndVersion() {
        JISOCreatorAttributes attributes = JISOCreatorAttributes.builder()
                .appName("JISOCreator")
                .appVersion("0.2.2-SNAPSHOT")
                .jvmVersion("21")
                .jvmVendor("Eclipse Adoptium")
                .osName("Linux")
                .build();

        assertEquals("JISOCreator version 0.2.2-SNAPSHOT", attributes.toString());
    }

    @Test
    @DisplayName("Should format toString(baseString) substituting appName/appVersion/jvmVersion/jvmVendor/osName in order")
    void shouldFormatCustomToStringWithBaseString() {
        JISOCreatorAttributes attributes = JISOCreatorAttributes.builder()
                .appName("JISOCreator")
                .appVersion("0.2.2-SNAPSHOT")
                .jvmVersion("21")
                .jvmVendor("Eclipse Adoptium")
                .osName("Linux")
                .build();

        String formatted = attributes.toString("%s|%s|%s|%s|%s");

        assertEquals("JISOCreator|0.2.2-SNAPSHOT|21|Eclipse Adoptium|Linux", formatted);
    }
}
