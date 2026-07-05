package cl.cavallinux.jisocreator.model.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;

@DisplayName("FallbackIsoFilesystemParser tests")
class FallbackIsoFilesystemParserTest {
    @Test
    @DisplayName("Should use primary parser result when deserialize succeeds")
    void shouldUsePrimaryWhenDeserializeSucceeds() {
        IsoFileSystem expected = new IsoFileSystem();
        FallbackIsoFilesystemParser parser = FallbackIsoFilesystemParser.builder()
                .primary(new StaticParser(Optional.of(expected), true))
                .fallback(new StaticParser(Optional.empty(), false))
                .build();

        Optional<IsoFileSystem> result = parser.deserialize("layout.xml");

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
    }

    @Test
    @DisplayName("Should use fallback parser when primary deserialize fails")
    void shouldUseFallbackWhenPrimaryDeserializeFails() {
        IsoFileSystem expected = new IsoFileSystem();
        FallbackIsoFilesystemParser parser = FallbackIsoFilesystemParser.builder()
                .primary(new StaticParser(Optional.empty(), true))
                .fallback(new StaticParser(Optional.of(expected), false))
                .build();

        Optional<IsoFileSystem> result = parser.deserialize("layout.xml");

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
    }

    @Test
    @DisplayName("Should not use fallback parser when deserialize fallback is disabled")
    void shouldNotUseFallbackWhenDeserializeFallbackIsDisabled() {
        CountingParser fallback = new CountingParser(Optional.of(new IsoFileSystem()), true);
        FallbackIsoFilesystemParser parser = FallbackIsoFilesystemParser.builder()
                .primary(new StaticParser(Optional.empty(), true))
                .fallback(fallback)
                .fallbackEnabled(false)
                .build();

        Optional<IsoFileSystem> result = parser.deserialize("layout.xml");

        assertTrue(result.isEmpty());
        assertEquals(0, fallback.deserializeCalls);
    }

    @Test
    @DisplayName("Should use fallback parser when primary serialize fails")
    void shouldUseFallbackWhenPrimarySerializeFails() {
        FallbackIsoFilesystemParser parser = FallbackIsoFilesystemParser.builder()
                .primary(new StaticParser(Optional.empty(), false))
                .fallback(new StaticParser(Optional.empty(), true))
                .build();

        boolean result = parser.serialize(new IsoFileSystem(), "layout.xml");

        assertTrue(result);
    }

    @Test
    @DisplayName("Should not use fallback parser when serialize fallback is disabled")
    void shouldNotUseFallbackWhenSerializeFallbackIsDisabled() {
        CountingParser fallback = new CountingParser(Optional.empty(), true);
        FallbackIsoFilesystemParser parser = FallbackIsoFilesystemParser.builder()
                .primary(new StaticParser(Optional.empty(), false))
                .fallback(fallback)
                .fallbackEnabled(false)
                .build();

        boolean result = parser.serialize(new IsoFileSystem(), "layout.xml");

        assertFalse(result);
        assertEquals(0, fallback.serializeCalls);
    }

    @Test
    @DisplayName("Should fail serialize when both primary and fallback fail")
    void shouldFailSerializeWhenBothParsersFail() {
        FallbackIsoFilesystemParser parser = FallbackIsoFilesystemParser.builder()
                .primary(new StaticParser(Optional.empty(), false))
                .fallback(new StaticParser(Optional.empty(), false))
                .build();

        boolean result = parser.serialize(new IsoFileSystem(), "layout.xml");

        assertFalse(result);
    }

    private static final class StaticParser implements IsoFilesystemParser<IsoFileSystem> {
        private final Optional<IsoFileSystem> deserializeResult;
        private final boolean serializeResult;

        private StaticParser(Optional<IsoFileSystem> deserializeResult, boolean serializeResult) {
            this.deserializeResult = deserializeResult;
            this.serializeResult = serializeResult;
        }

        @Override
        public Optional<IsoFileSystem> deserialize(String filePath) {
            return deserializeResult;
        }

        @Override
        public boolean serialize(IsoFileSystem isoFilesystem, String filePath) {
            return serializeResult;
        }
    }

    private static final class CountingParser implements IsoFilesystemParser<IsoFileSystem> {
        private final Optional<IsoFileSystem> deserializeResult;
        private final boolean serializeResult;
        private int deserializeCalls;
        private int serializeCalls;

        private CountingParser(Optional<IsoFileSystem> deserializeResult, boolean serializeResult) {
            this.deserializeResult = deserializeResult;
            this.serializeResult = serializeResult;
        }

        @Override
        public Optional<IsoFileSystem> deserialize(String filePath) {
            deserializeCalls++;
            return deserializeResult;
        }

        @Override
        public boolean serialize(IsoFileSystem isoFilesystem, String filePath) {
            serializeCalls++;
            return serializeResult;
        }
    }
}
