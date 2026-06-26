package cl.cavallinux.jisocreator.gui.dialog;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
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
import cl.cavallinux.jisocreator.model.isoexplorer.impl.IsoFileSystem;
import cl.cavallinux.jisocreator.model.parser.IsoFilesystemParser;
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
        newShell.setText(ShowIsoInformationDialogMessages.showIsoInfoDialogWindowTitle);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        log.info("Creating show info layout dialog area components");
        Composite area = (Composite) super.createDialogArea(parent);

        setTitle(ShowIsoInformationDialogMessages.showIsoInfoDialogWindowTitle);
        setMessage(ShowIsoInformationDialogMessages.showIsoInfoDialogStaticInfo);

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
        errorIndicator.setForeground(new Color(parent.getDisplay(), 255, 0, 0));
        GridData gdError = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        gdError.verticalIndent = 8;
        errorIndicator.setLayoutData(gdError);

        return area;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "Accept", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false);
    }

    @Override
    protected void okPressed() {
        String volumeIdInput = volumeIDText.getText();
        if (StringUtils.isNotBlank(volumeIdInput)) {
            if (volumeIdInput.length() <= IsoFilesystemParser.MKISOFS_VOLUMEID_MAXLENGTH) {
                setErrorMessage(null);
                errorIndicator.setText("");
                volumeIDResponse = volumeIdInput;
            } else {
                setErrorMessage("VolumeID is greater than 32 characters");
                errorIndicator.setText("This field must be 32 chars length");
                getShell().layout(true, true);
                getShell().pack();
                volumeIDText.setFocus();
                return;
            }
        } else {
            setErrorMessage("VolumeID is required");
            errorIndicator.setText("This field is required to complete request");
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