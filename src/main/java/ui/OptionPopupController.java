package ui;

import util.Database;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.annotation.PostConstruct;
import java.awt.*;
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

    @FXML
    public void openSettings() {
        JFXAlert setting = new JFXAlert((Stage) Center.getRootWindow());
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
        setting.show();
    }

    private void saveSetting(JFXAlert setting) {
        if (!(maxConcurrentField.validate() && waitTimeField.validate() && reconnectTimeField.validate())) {
            return;
        }

        int maxConcurrent = Integer.parseInt(maxConcurrentField.getText());
        Database.getInstance().setMaxConcurrentDownload(maxConcurrent);

        int failTime = Integer.parseInt(waitTimeField.getText());
        Database.getInstance().setFailConnectionWaitTime(failTime);

        int reconnectTime = Integer.parseInt(reconnectTimeField.getText());
        Database.getInstance().setReconnectionTimes(reconnectTime);

        // close the alert
        setting.hideWithAnimation();
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

    }

    private void makeSettingInput(JFXTextField textField, String label, String promptText) {
        Label promptLabel = new Label(label);
        promptLabel.setFont(new Font(10.0));
        textField.setValidators(new PositiveNumberValidator("Must be a positive number"));
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> textField.validate());
        textField.setPromptText(promptText);
        textField.setLabelFloat(true);

        settingRoot.getChildren().addAll(promptLabel, textField);
    }
}
