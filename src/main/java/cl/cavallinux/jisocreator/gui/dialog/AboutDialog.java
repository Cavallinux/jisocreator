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

import cl.cavallinux.jisocreator.gui.i18n.AboutDialogMessages;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AboutDialog extends TitleAreaDialog {
    private String formattedProgramVersion;

    @Builder
    protected AboutDialog(Shell parentShell) {
        super(parentShell);
        formattedProgramVersion = String.format(AboutDialogMessages.aboutDialogProgramVersion,
                AboutDialog.class.getPackage().getImplementationVersion(), System.getProperty("os.name"),
                System.getProperty("os.version"));
    }

    @Override
    protected void configureShell(Shell newShell) {
        log.info("Configuring about dialog shell");
        super.configureShell(newShell);
        newShell.setText(AboutDialogMessages.aboutDialogWindowTitle);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        return createAboutPanel(parent);
    }

    private Control createAboutPanel(Composite parent) {
        log.info("Creating about panel");
        Composite composite = new Composite(parent, SWT.NONE);
        setTitle("JIsocreator");
        setMessage(formattedProgramVersion);
        setTitleImage(ImageRegister.INSTANCE.getImageUtils().loadImage("iso72.png"));

        TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
        TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
        tabItem.setText(AboutDialogMessages.aboutDialogAboutTabText);
        Composite aboutComposite = new Composite(tabFolder, SWT.NONE);
        Label label = new Label(aboutComposite, SWT.NONE);
        label.setImage(ImageRegister.INSTANCE.getImageUtils().loadImage("iso128.png"));
        Label textClabel = new Label(aboutComposite, SWT.NONE);
        textClabel.setText(AboutDialogMessages.aboutDialogAboutCompositeText);

        GridDataFactory.defaultsFor(label).grab(true, true).span(1, 2).applyTo(label);
        GridDataFactory.defaultsFor(textClabel).grab(true, true).span(1, 2).applyTo(textClabel);
        GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(aboutComposite);
        tabItem.setControl(aboutComposite);

        tabItem = new TabItem(tabFolder, SWT.NONE);
        Label styledText = new Label(tabFolder, SWT.V_SCROLL | SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL);
        styledText.setText(AboutDialogMessages.aboutDialogLicenseCompositeText);

        tabItem.setText(AboutDialogMessages.aboutDialogLicenseTabText);
        tabItem.setControl(styledText);

        GridDataFactory.defaultsFor(tabFolder).grab(true, true).applyTo(tabFolder);
        GridLayoutFactory.swtDefaults().generateLayout(composite);

        GridDataFactory.defaultsFor(composite).grab(true, true).applyTo(composite);
        GridLayoutFactory.swtDefaults().generateLayout(parent);
        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        log.info("Creating buttons for about dialog");
        createButton(parent, IDialogConstants.OK_ID, AboutDialogMessages.aboutDialogOKButtonText, true);
    }
}