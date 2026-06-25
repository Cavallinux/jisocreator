package cl.cavallinux.jisocreator.instances;

import cl.cavallinux.jisocreator.model.cmdline.ICommandLineParser;
import cl.cavallinux.jisocreator.model.cmdline.JISOCreatorCommandLineParser;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandLineParserManager {
    INSTANCE(JISOCreatorCommandLineParser.builder().build());

    private ICommandLineParser parser;
}
