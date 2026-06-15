package cl.cavallinux.jisocreator.instances;

import org.apache.commons.cli.Option;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandLineOptionsManager {
    HELP(Option.builder("h").longOpt("help").desc("Show this help message").build()),
    LOAD(Option.builder("l").longOpt("load").desc("Load existing xml file into GUI").hasArg().build()),
    DEBUG(Option.builder("d").longOpt("debug").desc("Start debug mode").build()),
    ISOINPUT(Option.builder("i").longOpt("input").desc("ISO XML Layout to load").required(false).hasArg().build()),
    ISOOUTPUT(Option.builder("o").longOpt("output").desc("ISO File output path").required(false).hasArg().build()),
    VERSION(Option.builder("v").longOpt("version").desc("Show application version and jvm version").build());

    private Option option;
}
