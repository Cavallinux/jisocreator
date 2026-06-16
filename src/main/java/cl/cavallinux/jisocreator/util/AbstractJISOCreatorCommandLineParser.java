package cl.cavallinux.jisocreator.util;

import org.apache.commons.cli.CommandLineParser;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public abstract class AbstractJISOCreatorCommandLineParser {
    protected CommandLineParser commandLineParser;
}
