package cl.cavallinux.jisocreator.action.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import cl.cavallinux.jisocreator.action.jobs.SaveISO9660ImageThread;
import cl.cavallinux.jisocreator.gui.sashfom.IsoExplorerSashForm;
import cl.cavallinux.jisocreator.gui.window.MainWindow;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.util.IOUtils;
import cl.cavallinux.jisocreator.util.ImageUtils;

public class SaveAsIsoAction extends Action {
    private static SaveAsIsoAction instance;
    private List<String> mkisofsCommand;

    private SaveAsIsoAction() {
	super("ISO9660 File", ImageUtils.getInstance().loadImageDescriptor("x-cd-image.png"));
	setToolTipText("Save the actual layout into a ISO9660 file");
    }

    @Override
    public void run() {
	try {
	    setMkisofsCommand();
	    FileDialog openXMLDialog = new FileDialog(MainWindow.getInstance().getShell(), SWT.SAVE);
	    {
		openXMLDialog.setText("Choose a iso file to save");
		openXMLDialog.setOverwrite(true);
		openXMLDialog.setFileName("iso.iso");
		openXMLDialog.setFilterExtensions(new String[] { "*.iso" });
		openXMLDialog.setFilterNames(new String[] { "ISO9660 CD-ROM Files" });
	    }
	    String path = openXMLDialog.open();
	    boolean pathIsNull = (path == null);
	    if (pathIsNull) {
		return;
	    }
	    if (new File(path).exists()) {
		new File(path).delete();
	    }
	    mkisofsCommand.add(path);
	    IsoFileSystem root = ((IsoFileSystem) IsoExplorerSashForm.getInstance().getIsoDirectoriesTree().getInput());
	    root.parse();

	    for (String temp : root.getPaths()) {
		mkisofsCommand.add(temp);
	    }
	    ProcessBuilder mkisofsProcessBuilder = new ProcessBuilder(mkisofsCommand);
	    Process isoProcess = mkisofsProcessBuilder.start();
	    SaveISO9660ImageThread saveThread = new SaveISO9660ImageThread(isoProcess.getErrorStream());
	    saveThread.start();
	} catch (IOException e) {
	    Logger.getAnonymousLogger().log(Level.SEVERE, "Error executing command", e);
	}
    }

    public static SaveAsIsoAction getInstance() {
	if (instance == null) {
	    instance = new SaveAsIsoAction();
	}
	return instance;
    }

    private void setMkisofsCommand() {
	mkisofsCommand = new ArrayList<String>();
	mkisofsCommand.add(IOUtils.getInstance().getStore().getString("mkisofs.path"));
	mkisofsCommand.add("-graft-points");
	mkisofsCommand.add("-gui");
	if (IOUtils.getInstance().getStore().getBoolean("mkisofs.joliet.use")) {
	    mkisofsCommand.add("-J");
	    mkisofsCommand.add("-joliet-long");

	    if (IOUtils.getInstance().getStore().getBoolean("mkisofs.rockridge.use")) {
		mkisofsCommand.add("-r");
	    }
	}

	if (IOUtils.getInstance().getStore().getBoolean("mkisofs.symlinks.follow")) {
	    mkisofsCommand.add("-f");
	}
	mkisofsCommand.add("-l");
	mkisofsCommand.add("-D");
	mkisofsCommand.add("-no-bak");
	mkisofsCommand.add("-iso-level");
	mkisofsCommand.add("2");
	mkisofsCommand.add("-o");
    }
}