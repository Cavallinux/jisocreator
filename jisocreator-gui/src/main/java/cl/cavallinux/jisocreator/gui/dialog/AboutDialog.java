package cl.cavallinux.jisocreator.gui.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import cl.cavallinux.jisocreator.util.ImageUtils;

public class AboutDialog extends TitleAreaDialog {

    public AboutDialog(Shell parentShell) {
	super(parentShell);
    }

    @Override
    protected void configureShell(Shell newShell) {
	super.configureShell(newShell);
	newShell.setText("About");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
	return createAboutPanel(parent);
    }

    private Control createAboutPanel(Composite parent) {
	StringBuffer stringBuffer = new StringBuffer();
	stringBuffer.append("Version: ");
	stringBuffer.append("0.0.0\n");
	stringBuffer.append("Running in ");
	stringBuffer.append(System.getProperty("os.name"));
	stringBuffer.append(" ");
	stringBuffer.append(System.getProperty("os.version"));

	Composite composite = new Composite(parent, SWT.NONE);
	setTitle("JIsocreator");
	setMessage(stringBuffer.toString());
	setTitleImage(ImageUtils.getInstance().loadImage("iso72.png"));

	TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
	TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
	tabItem.setText("About");
	Composite aboutComposite = new Composite(tabFolder, SWT.NONE);
	Label label = new Label(aboutComposite, SWT.NONE);
	label.setImage(ImageUtils.getInstance().loadImage("iso128.png"));
	Label textClabel = new Label(aboutComposite, SWT.NONE);

	stringBuffer = new StringBuffer();
	stringBuffer.append("JIsoCreator is a ISO9660 CD Image creator, frontend of MKISOFS");
	stringBuffer.append(
		"\nIt combines the platform independent of Java,\nwith a fast native graphical user interface.");
	stringBuffer.append("\n\n\nThis program use the following third-party libraries");
	textClabel.setText(stringBuffer.toString());

	GridDataFactory.defaultsFor(label).grab(true, true).span(1, 2).applyTo(label);
	GridDataFactory.defaultsFor(textClabel).grab(true, true).span(1, 2).applyTo(textClabel);
	GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(aboutComposite);
	tabItem.setControl(aboutComposite);

	stringBuffer = new StringBuffer();
	stringBuffer.append("This program is free software: you can redistribute it and/or modify\n");
	stringBuffer.append("it under the terms of the GNU General Public License as published by\n");
	stringBuffer.append("the Free Software Foundation, either version 3 of the License, or\n");
	stringBuffer.append("(at your option) any later version.\n\n");
	stringBuffer.append("This program is distributed in the hope that it will be useful,\n");
	stringBuffer.append("but WITHOUT ANY WARRANTY; without even the implied warranty of\n");
	stringBuffer.append("MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n");
	stringBuffer.append("GNU General Public License for more details.\n\n");
	stringBuffer.append("You should have received a copy of the GNU General Public License\n");
	stringBuffer.append("along with this program.  If not, see <http://www.gnu.org/licenses/>.");

	tabItem = new TabItem(tabFolder, SWT.NONE);
	Label styledText = new Label(tabFolder, SWT.V_SCROLL | SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL);
	styledText.setText(stringBuffer.toString());

	tabItem.setText("License");
	tabItem.setControl(styledText);

	GridDataFactory.defaultsFor(tabFolder).grab(true, true).applyTo(tabFolder);
	GridLayoutFactory.swtDefaults().generateLayout(composite);

	GridDataFactory.defaultsFor(composite).grab(true, true).applyTo(composite);
	GridLayoutFactory.swtDefaults().generateLayout(parent);
	return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
	createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }
}