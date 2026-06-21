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

import cl.cavallinux.jisocreator.gui.listeners.dialog.EnterKeySubmitAdapter;
import cl.cavallinux.jisocreator.model.parser.IsoFilesystemParser;
import lombok.Getter;

@Getter
public class ShowIsoLayoutInformationDialog extends TitleAreaDialog {
    private final String windowTitle;
    private final String bannerInfo;
    private final String isoFilesystemVolumeID;
    private final String isoFilesystemApplicationID;
    private final String isoFilesystemLength;
    private final String isoFilesystemPublisherID;
    private Text volumeIDText;
    private Label errorIndicator;
    private String volumeIDResponse;

    public ShowIsoLayoutInformationDialog(Shell parentShell, String windowTitle, String bannerInfo,
            String isoFilesysteVolumeID, String isoFilesystemApplicationID, String isoFilesystemLength,
            String isoFilesystemPublisherID) {
        super(parentShell);
        this.windowTitle = windowTitle;
        this.bannerInfo = bannerInfo;
        this.isoFilesystemVolumeID = isoFilesysteVolumeID;
        this.isoFilesystemApplicationID = isoFilesystemLength;
        this.isoFilesystemLength = isoFilesystemApplicationID;
        this.isoFilesystemPublisherID = isoFilesystemPublisherID;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(windowTitle);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);

        setTitle(windowTitle);
        setMessage(bannerInfo);

        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        container.setLayout(new GridLayout(2, false));

        Label lblVolumeId = new Label(container, SWT.NONE);
        lblVolumeId.setText("Volume id:");
        lblVolumeId.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        volumeIDText = new Text(container, SWT.BORDER | SWT.SINGLE);
        volumeIDText.setText(isoFilesystemVolumeID);
        volumeIDText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        volumeIDText.addSelectionListener(new EnterKeySubmitAdapter(this::okPressed));

        Label lblAppId = new Label(container, SWT.NONE);
        lblAppId.setText("Application ID ");
        lblAppId.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        Label txtAppIdDummy = new Label(container, SWT.SINGLE | SWT.READ_ONLY);
        txtAppIdDummy.setText(isoFilesystemApplicationID);
        txtAppIdDummy.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label publisherIDLabel = new Label(container, SWT.NONE);
        publisherIDLabel.setText("Publisher ID ");
        publisherIDLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        Label publisherIDDataLabel = new Label(container, SWT.SINGLE | SWT.READ_ONLY);
        publisherIDDataLabel.setText(isoFilesystemPublisherID);
        publisherIDDataLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label lblImageSize = new Label(container, SWT.NONE);
        lblImageSize.setText("Iso layout size ");
        lblImageSize.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        Label txtImageSize = new Label(container, SWT.SINGLE | SWT.READ_ONLY);
        txtImageSize.setText(isoFilesystemLength.concat(" bytes"));
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