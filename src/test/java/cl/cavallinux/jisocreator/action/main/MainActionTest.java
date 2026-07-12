package cl.cavallinux.jisocreator.action.main;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.cavallinux.jisocreator.model.cmdline.ICommandLineParser;
import cl.cavallinux.jisocreator.model.cmdline.JISOCreatorCommandLineParser;

@DisplayName("MainAction tests")
class MainActionTest {
    @Test
    @DisplayName("Should load layout path and run GUI branch when load option is present")
    void shouldLoadLayoutPathAndRunGuiBranchWhenLoadOptionIsPresent() throws Exception {
        ICommandLineParser parser = JISOCreatorCommandLineParser.builder().build();
        TestableMainAction action = new TestableMainAction(parser);

        action.handleCommandLine(new String[] { "--load", "layout.xml" });

        assertEquals("layout.xml", action.getLayoutFilePath());
        assertEquals(1, action.runCalls);
    }

    @Test
    @DisplayName("Should run GUI branch by default when no options are present")
    void shouldRunGuiBranchByDefaultWhenNoOptionsArePresent() throws ParseException, java.io.IOException {
        ICommandLineParser parser = JISOCreatorCommandLineParser.builder().build();
        TestableMainAction action = new TestableMainAction(parser);

        action.handleCommandLine(new String[0]);

        assertEquals(1, action.runCalls);
    }

    private static final class TestableMainAction extends MainAction {
        private int runCalls = 0;

        private TestableMainAction(ICommandLineParser parser) {
            super(parser, null);
        }

        @Override
        public void run() {
            runCalls++;
        }
    }
}
