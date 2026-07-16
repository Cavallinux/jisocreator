package cl.cavallinux.jisocreator.model.cmdline;

import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.help.HelpFormatter;
import org.apache.commons.cli.help.TableDefinition;
import org.apache.commons.cli.help.TextStyle;

import cl.cavallinux.jisocreator.gui.i18n.CommandLineMessages;

public class JISOCreatorCommandLineHelpFormatter extends HelpFormatter {
    private static final List<String> DEFAULT_COLUMN_TEXTS = List.of(
            CommandLineMessages.commandLineIsoOptionsTableOptionColumnMessage,
            CommandLineMessages.commandLineIsoOptionsTableDescriptionColumnMessage);
    private static final String DEFAULT_OPTIONS_TABLE_TITLE = CommandLineMessages.commandLineIsoOptionsTableTitleMessage;

    protected JISOCreatorCommandLineHelpFormatter(Builder builder) {
        super(builder);
        setSyntaxPrefix(CommandLineMessages.commandLineIsoOptionsSyntaxHeaderMessage);
    }

    @Override
    public TableDefinition getTableDefinition(Iterable<Option> options) {
        TableDefinition tableDefinition = super.getTableDefinition(options);
        List<TextStyle> columnTextStyles = tableDefinition.columnTextStyles();
        Iterable<List<String>> tableRows = tableDefinition.rows();
        return TableDefinition.from(DEFAULT_OPTIONS_TABLE_TITLE, columnTextStyles, DEFAULT_COLUMN_TEXTS, tableRows);
    }
    
    @Override
    public void printHelp(String cmdLineSyntax, String header, Options options, String footer, boolean autoUsage)
            throws IOException {
        super.printHelp(cmdLineSyntax, header, options, footer, autoUsage);
    }
}
