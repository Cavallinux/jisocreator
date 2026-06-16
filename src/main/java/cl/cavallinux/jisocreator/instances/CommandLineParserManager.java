package cl.cavallinux.jisocreator.instances;

import org.apache.commons.cli.DefaultParser;

import cl.cavallinux.jisocreator.util.JISOCreatorCommandLineParser;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandLineParserManager {
    INSTANCE(JISOCreatorCommandLineParser.builder().commandLineParser(new DefaultParser())
            .header(JISOCreatorCommandLineParser.buildHelpHeader())
            .footer(JISOCreatorCommandLineParser.buildHelpFooter()).options(JISOCreatorCommandLineParser.buildOptions())
            .build());

    private JISOCreatorCommandLineParser parser;
}
