package cl.cavallinux.jisocreator.instances;

import org.apache.commons.cli.Option;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandLineOptionsManager {
    LOAD(Option.builder("l").longOpt("load").desc("Load existing xml file into GUI").hasArg().argName("xmllayout")
            .build()),
    HELP(Option.builder("h").longOpt("help").desc("Show this help message").build()),
    VERSION(Option.builder("v").longOpt("version").desc("Show application version and jvm version").build()),
    ISOINPUT(Option.builder("i").longOpt("input").desc("ISO XML Layout to load").hasArg().argName("xmllayout").build()),
    ISOOUTPUT(Option.builder("o").longOpt("output").desc("ISO File output path").hasArg().argName("isoFile").build());

    private Option option;
}
