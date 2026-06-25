package cl.cavallinux.jisocreator.model.cmdline;

import org.apache.commons.cli.CommandLineParser;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public abstract class AbstractJISOCreatorCommandLineParser implements ICommandLineParser {
    protected CommandLineParser commandLineParser;
}
