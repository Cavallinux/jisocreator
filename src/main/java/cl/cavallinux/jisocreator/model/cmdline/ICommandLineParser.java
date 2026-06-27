package cl.cavallinux.jisocreator.model.cmdline;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

public interface ICommandLineParser {
    default CommandLine parse(String... commandLineArguments) throws ParseException {
        throw new ParseException("Default implementation");
    };

    void handleCommandLine(CommandLine cmd) throws ParseException;

    void printVersion();

    void printHelp(String programName) throws IOException;
}
