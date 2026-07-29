package cl.cavallinux.jisocreator.model.cmdline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import cl.cavallinux.jisocreator.gui.i18n.CommandLineMessages;
import cl.cavallinux.jisocreator.instances.CommandLineOptionsManager;

public interface ICommandLineParser {
    default CommandLine parse(String... commandLineArguments) throws ParseException {
        throw new ParseException("Default implementation");
    };
    
    static JISOCreatorAttributes buildAttributes() {
        Package commandLinePackage = JISOCreatorCommandLineParser.class.getPackage();
        return JISOCreatorAttributes.builder().appName(commandLinePackage.getSpecificationTitle())
                .appVersion(commandLinePackage.getImplementationVersion())
                .jvmVersion(System.getProperty("java.version"))
                .jvmVendor(System.getProperty("java.specification.vendor")).osName(System.getProperty("os.name"))
                .osVersion(System.getProperty("os.version"))
                .build();
    }

    void handleCommandLine(CommandLine cmd) throws ParseException;

    void printVersion();

    void printHelp(String programName) throws IOException;
    
    void printLicense();
    
    default String buildHelpHeader(JISOCreatorAttributes attributes) {
        List<String> helpHeaderArguments = new ArrayList<String>();
        helpHeaderArguments.add(attributes.appName());
        return String.format(CommandLineMessages.commandLineAppDescriptionMessage, helpHeaderArguments.toArray());
    }

    default String buildHelpFooter(JISOCreatorAttributes attributes) {
        String lowerCaseAppName = StringUtils.toRootLowerCase(attributes.appName());
        return Strings.CI.replace(CommandLineMessages.commandLineExampleUsageMessage, "%s", lowerCaseAppName);
    }

    default Options buildOptions() {
        Options options = new Options();
        options.addOption(CommandLineOptionsManager.ISOINPUT.getOption());
        options.addOption(CommandLineOptionsManager.ISOOUTPUT.getOption());
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.addOption(CommandLineOptionsManager.LOAD.getOption());
        optionGroup.addOption(CommandLineOptionsManager.HELP.getOption());
        optionGroup.addOption(CommandLineOptionsManager.VERSION.getOption());
        optionGroup.addOption(CommandLineOptionsManager.LICENSE.getOption());
        options.addOptionGroup(optionGroup);
        return options;
    }
}
