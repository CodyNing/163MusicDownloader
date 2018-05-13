package ui;

import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import util.Database;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class OptionPopupController implements Initializable {

    private VBox settingRoot;

    private final JFXTextField maxConcurrentField = new JFXTextField(String.valueOf(Database.getInstance().getMaxConcurrentDownload()));
    private final JFXTextField waitTimeField = new JFXTextField(String.valueOf(Database.getInstance().getFailConnectionWaitTime()));
    private final JFXTextField reconnectTimeField = new JFXTextField(String.valueOf(Database.getInstance().getReconnectionTimes()));

    private final JFXTextField downloadFolderField = new JFXTextField(Database.getSongDir().getAbsolutePath());
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    private final JFXButton browseButton = new JFXButton("Browse");
    private JFXAlert setting;
    private boolean isInit = false;

    @PostConstruct
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settingRoot = new VBox();
        settingRoot.setSpacing(20.0);

        makeSettingInput(maxConcurrentField,
                "This number will determine the number of songs get download at the same time\n(Making this number too large may cause the server refuse connection)",
                "Maximum Number of Concurrent Download");
        makeSettingInput(waitTimeField,
                "Time to wait before reconnection whenever download connection failed",
                "Wait Time (sec)");
        makeSettingInput(reconnectTimeField,
                "Maximum number of reconnection of a single download\nDownload will be cancelled if reconnection failed times is more than this number",
                "Times of Reconnection");

        setFolderBrowser();
    }

    @FXML
    public void openSettings() {
        if (!isInit) {
            setting = new JFXAlert((Stage) Center.getRootWindow());
            setting.setSize(700, 600);
            setting.initModality(Modality.APPLICATION_MODAL);
            setting.setOverlayClose(false);

            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Label("Setting"));

            layout.setBody(settingRoot);

            JFXButton acceptButton = new JFXButton("SAVE");
            acceptButton.getStyleClass().add("dialog-accept");
            acceptButton.setOnAction(event -> saveSetting(setting));

            JFXButton closeButton = new JFXButton("CANCEL");
            closeButton.setOnAction(event -> setting.hideWithAnimation());

            layout.setActions(acceptButton, closeButton);

            setting.setContent(layout);

            isInit = true;
        }
        setting.show();
    }

    private void saveSetting(JFXAlert setting) {
        if (!(maxConcurrentField.validate() && waitTimeField.validate() && reconnectTimeField.validate() && downloadFolderField.validate())) {
            return;
        }

        int maxConcurrent = Integer.parseInt(maxConcurrentField.getText());
        Database.getInstance().setMaxConcurrentDownload(maxConcurrent);

        int failTime = Integer.parseInt(waitTimeField.getText());
        Database.getInstance().setFailConnectionWaitTime(failTime);

        int reconnectTime = Integer.parseInt(reconnectTimeField.getText());
        Database.getInstance().setReconnectionTimes(reconnectTime);

        File folder = new File(downloadFolderField.getText());
        Database.setSongDir(folder);

        // close the alert
        setting.hideWithAnimation();
    }

    private void setFolderBrowser() {
        browseButton.setOnAction(event -> {
            directoryChooser.setTitle("Choose Download Folder");
            File dir = directoryChooser.showDialog(Center.getRootWindow());
            if (dir != null)
            	downloadFolderField.setText(dir.getAbsolutePath());
        });
        browseButton.setStyle("-fx-text-fill:WHITE;-fx-background-color:#5264AE;-fx-font-size:14px;");
        downloadFolderField.setPromptText("Download Folder");
        downloadFolderField.setLabelFloat(true);
        downloadFolderField.setValidators(new DirectoryValidator("Path is invalid"));
        downloadFolderField.focusedProperty().addListener((observable, oldValue, newValue) -> downloadFolderField.validate());
        HBox hBox = new HBox(downloadFolderField, browseButton);
        hBox.setSpacing(10.0);
        settingRoot.getChildren().addAll(
                new Label("Choose the folder that store the songs\n" +
                        "(Every time you change it, the song will be copied over automatically)"),
                hBox
        );
    }

    @FXML
    public void about() {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/CRonYii/163MusicDownloader"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void exit() {
        Center.CLOSE_EVENT.handle(new WindowEvent(Center.getRootWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
        Platform.exit();
    }


    private void makeSettingInput(JFXTextField textField, String label, String promptText) {
        textField.setFont(new Font(12.0));

        Label promptLabel = new Label(label);
        promptLabel.setFont(new Font(12.0));
        textField.setValidators(new PositiveNumberValidator("Must be a positive number"));
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> textField.validate());
        textField.setPromptText(promptText);
        textField.setLabelFloat(true);

        settingRoot.getChildren().addAll(promptLabel, textField);
    }

    private void makeSettingToggle(JFXToggleButton toggleButton, String promptText) {
        // TODO
    }

}
