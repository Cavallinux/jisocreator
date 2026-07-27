package cl.cavallinux.jisocreator.gui.dialog;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import cl.cavallinux.jisocreator.gui.i18n.ShowIsoInformationDialogMessages;
import cl.cavallinux.jisocreator.gui.listeners.dialog.EnterKeySubmitAdapter;
import cl.cavallinux.jisocreator.gui.theme.DarkThemeSupport;
import cl.cavallinux.jisocreator.instances.ImageRegister;
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.model.parser.decl.IsoFilesystemParser;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Setter
public class ShowIsoLayoutInformationDialog extends TitleAreaDialog {
    private IsoFileSystem isoFileSystem;
    private Text volumeIDText;
    private Label errorIndicator;
    private String volumeIDResponse;

    @Builder
    protected ShowIsoLayoutInformationDialog(Shell parentShell, IsoFileSystem isoFileSystem) {
        super(parentShell);
        this.isoFileSystem = isoFileSystem;
    }

    @Override
    protected void configureShell(Shell newShell) {
        log.info("Configuring show info layout shell");
        super.configureShell(newShell);
        DarkThemeSupport.enableWindowsDarkMode(newShell.getDisplay());
        newShell.setText(ShowIsoInformationDialogMessages.showIsoInfoDialogWindowTitle);
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        // TitleAreaDialog builds its title banner (icon/title/message) as a sibling of
        // dialogArea/buttonBar inside createContents(), with its own explicit colors
        // (JFaceColors.setColors(...)). Neither applyToControlTree(dialogArea) nor
        // applyToControlTree(buttonBarComposite) reach it, so the full contents tree
        // must be re-styled here once everything is built (same fix applied to
        // AboutDialog, the only other TitleAreaDialog subclass in this codebase).
        DarkThemeSupport.applyToControlTree(contents);
        // applyToControlTree unconditionally resets every control's foreground to the
        // dark palette color, which would undo the intentional red error-text color
        // applied in createDialogArea() below - restore it here, after the fact.
        if (Objects.nonNull(errorIndicator) && !errorIndicator.isDisposed()) {
            errorIndicator.setForeground(contents.getDisplay().getSystemColor(SWT.COLOR_RED));
        }
        return contents;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        log.info("Creating show info layout dialog area components");
        Composite area = (Composite) super.createDialogArea(parent);

        setTitle(ShowIsoInformationDialogMessages.showIsoInfoDialogWindowTitle);
        setMessage(ShowIsoInformationDialogMessages.showIsoInfoDialogStaticInfo);
        // TitleAreaDialog falls back to JFace's stock DLG_IMG_TITLE_BANNER image
        // whenever no custom titleAreaImage is set - that stock banner graphic has a
        // light background baked into the bitmap itself, so no amount of
        // setBackground()/applyToControlTree() styling can dark-theme it. Using the
        // app icon here (same pattern as AboutDialog) replaces that fixed light image.
        setTitleImage(ImageRegister.INSTANCE.getImageUtils().loadImage("jisocreator.svg"));

        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        container.setLayout(new GridLayout(2, false));

        Label lblVolumeId = new Label(container, SWT.NONE);
        lblVolumeId.setText(ShowIsoInformationDialogMessages.showIsoInfoDialogVolumeID);
        lblVolumeId.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        volumeIDText = new Text(container, SWT.BORDER | SWT.SINGLE);
        volumeIDText.setText(isoFileSystem.getVolumeID());
        volumeIDText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        volumeIDText.addSelectionListener(new EnterKeySubmitAdapter(this::okPressed));

        Label lblAppId = new Label(container, SWT.NONE);
        lblAppId.setText(ShowIsoInformationDialogMessages.showIsoInfoDialogApplicationID);
        lblAppId.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        Label txtAppIdDummy = new Label(container, SWT.SINGLE | SWT.READ_ONLY);
        txtAppIdDummy.setText(isoFileSystem.getApplicationID());
        txtAppIdDummy.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label publisherIDLabel = new Label(container, SWT.NONE);
        publisherIDLabel.setText(ShowIsoInformationDialogMessages.showIsoInfoDialogPublisherID);
        publisherIDLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        Label publisherIDDataLabel = new Label(container, SWT.SINGLE | SWT.READ_ONLY);
        publisherIDDataLabel.setText(isoFileSystem.getPublisherID());
        publisherIDDataLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label lblImageSize = new Label(container, SWT.NONE);
        lblImageSize.setText(ShowIsoInformationDialogMessages.showIsoInfoDialogIsoSize);
        lblImageSize.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        Label txtImageSize = new Label(container, SWT.SINGLE | SWT.READ_ONLY);
        txtImageSize.setText(String.valueOf(isoFileSystem.getIsoLength()).concat(" bytes"));
        txtImageSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        errorIndicator = new Label(container, SWT.NONE);
        errorIndicator.setText("");
        errorIndicator.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));
        GridData gdError = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        gdError.verticalIndent = 8;
        errorIndicator.setLayoutData(gdError);

        DarkThemeSupport.applyToControlTree(area);
        errorIndicator.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));

        return area;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, ShowIsoInformationDialogMessages.showIsoInfoDialogAcceptText,
                true);
        createButton(parent, IDialogConstants.CANCEL_ID, ShowIsoInformationDialogMessages.showIsoInfoDialogCancelText,
                false);
        DarkThemeSupport.applyToControlTree(parent);
    }

    @Override
    protected void okPressed() {
        String volumeIdInput = volumeIDText.getText();
        if (StringUtils.isNotBlank(volumeIdInput)) {
            if (volumeIdInput.length() <= IsoFilesystemParser.MKISOFS_VOLUMEID_MAXLENGTH) {
                errorIndicator.setText(StringUtils.EMPTY);
                volumeIDResponse = volumeIdInput;
            } else {
                errorIndicator.setText(ShowIsoInformationDialogMessages.showIsoInfoDialogVolumeIDGreaterThanMaxMessage);
                getShell().layout(true, true);
                getShell().pack();
                volumeIDText.setFocus();
                return;
            }
        } else {
            errorIndicator.setText(ShowIsoInformationDialogMessages.showIsoInfoDialogIncompleteVolumeIDMessage);
            getShell().layout(true, true);
            getShell().pack();
            volumeIDText.setFocus();
            return;
        }
        super.okPressed();
    }

    @Override
    protected Point getInitialSize() {
        Point computedSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        return new Point(Math.max(computedSize.x, 450), computedSize.y);
    }
}