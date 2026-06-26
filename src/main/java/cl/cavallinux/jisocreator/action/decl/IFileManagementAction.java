package cl.cavallinux.jisocreator.action.decl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.FileDialog;

import cl.cavallinux.jisocreator.gui.i18n.MainActionsMessages;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.instances.GUIManager;

public interface IFileManagementAction {
    final String XML_FILE_EXTENSION = ".xml";
    final String ISO_FILE_EXTENSION = ".iso";
    final String ISO_FILE_NAMES = MainActionsMessages.saveAsIsoActionFileNameText;
    final String ISO_DIALOG_TITLE = MainActionsMessages.saveAsIsoActionFileDialogTitle;
    final String XML_FILE_NAMES = MainActionsMessages.saveAsXMLActionFileNameText;
    final String XML_DIALOG_TITLE = MainActionsMessages.saveAsIsoActionFileDialogTitle;
    
    default String obtainAbsolutePathFile(String fileName, String dialogFileFilterExtension, String fileDialogTitle,
            String dialogFileFilterName, int fileDialogStyle) {
        MainWindow mainWindow = GUIManager.INSTANCE.getMainWindow();
        FileDialog fileDialog = new FileDialog(mainWindow.getShell(), fileDialogStyle);
        fileDialog.setText(fileDialogTitle);
        fileDialog.setOverwrite(true);
        fileDialog.setFileName(fileName);
        fileDialog.setFilterExtensions(obtainFileFilterNames(dialogFileFilterExtension));
        fileDialog.setFilterNames(obtainFileDialogExtensions(dialogFileFilterName));
        return fileDialog.open();
    }

    default String[] obtainFileFilterNames(String fileName) {
        List<String> fileFilterNamesList = new ArrayList<>();
        fileFilterNamesList.add(fileName);
        return fileFilterNamesList.toArray(String[]::new);
    }

    default String[] obtainFileDialogExtensions(String fileExtensions) {
        List<String> isoFileExtensionsList = new ArrayList<>();
        isoFileExtensionsList.add("*".concat(fileExtensions));
        return isoFileExtensionsList.toArray(String[]::new);
    }
}
