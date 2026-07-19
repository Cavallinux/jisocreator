package cl.cavallinux.jisocreator.instances;

import org.apache.commons.cli.Option;

import cl.cavallinux.jisocreator.gui.i18n.CommandLineMessages;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandLineOptionsManager {
    LOAD(Option.builder("l").longOpt("load").desc(CommandLineMessages.commandLineLoadOptionDescriptionMessage).hasArg()
            .argName("xmllayout").since(CommandLineOptionsManager.class.getPackage().getImplementationVersion()).get()),
    HELP(Option.builder("h").longOpt("help").desc(CommandLineMessages.commandLineHelpOptionDescriptionMessage)
            .since(CommandLineOptionsManager.class.getPackage().getImplementationVersion()).get()),
    VERSION(Option.builder("v").longOpt("version").desc(CommandLineMessages.commandLineVersionOptionDescriptionMessage)
            .since(CommandLineOptionsManager.class.getPackage().getImplementationVersion()).get()),
    LICENSE(Option.builder("L").longOpt("license").desc(CommandLineMessages.commandLineLicenseOptionDescriptionMessage)
            .since(CommandLineOptionsManager.class.getPackage().getImplementationVersion()).get()),
    ISOINPUT(Option.builder("i").longOpt("input").desc(CommandLineMessages.commandLineIsoInputOptionDescriptionMessage)
            .hasArg().argName("xmllayout")
            .since(CommandLineOptionsManager.class.getPackage().getImplementationVersion()).get()),
    ISOOUTPUT(Option.builder("o").longOpt("output")
            .desc(CommandLineMessages.commandLineIsoOutputOptionDescriptionMessage).hasArg().argName("isoFile")
            .since(CommandLineOptionsManager.class.getPackage().getImplementationVersion()).get());

    private Option option;
}
