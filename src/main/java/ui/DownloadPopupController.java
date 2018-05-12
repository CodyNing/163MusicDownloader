package ui;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.Database;

import java.awt.*;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadPopupController {

    @FXML
    public void downloadPlaylist() {
        promptDialog(new DownloadEvent.PlaylistDownloadEvent(), "playlist", "All songs in the playlist will be downloaded");
    }

    @FXML
    public void downloadSong() {
        promptDialog(new DownloadEvent.SongDownloadEvent(), "song", "The Song will be downloaded");
    }

    @FXML
    public void downloadAlbum() {
        promptDialog(new DownloadEvent.AlbumDownloadEvent(), "album", "All songs in the album will be downloaded");
    }

    @FXML
    public void downloadArtist() {
        promptDialog(new DownloadEvent.ArtistDownloadEvent(), "artist", "All songs of artist will be downloaded");
    }

    @FXML
    public void openSongsFolder() {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(Database.getSongDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void promptDialog(DownloadEvent task, String tag, String promptMsg) {
        JFXAlert alert = new JFXAlert((Stage) Center.getRootWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new javafx.scene.control.Label("Enter an id number"));

        Label promptLabel = new Label(promptMsg);
        JFXTextField textField = new JFXTextField();
        textField.setValidators(new PositiveNumberValidator("id must be a number"));
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // use regex to fetch song id from url if necessary
            String id = textField.getText().trim();
            if (!id.matches("^\\d*$")) {
                String regex = tag + "\\?id=(\\d*)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(id);
                if (matcher.find())
                    id = matcher.group(1);
            }
            textField.setText(id);

            textField.validate();
        });
        textField.setPromptText(tag.substring(0, 1).toUpperCase() + tag.substring(1) + " ID");
        textField.setLabelFloat(true);
        VBox body = new VBox(promptLabel, textField);
        body.setSpacing(20.0);

        layout.setBody(body);

        JFXButton acceptButton = new JFXButton("ACCEPT");
        acceptButton.getStyleClass().add("dialog-accept");
        acceptButton.setOnAction(event -> {
            if (textField.validate()) {
                // Start a new Thread to download the song in background
                String id = textField.getText();
                alert.hideWithAnimation();

                Center.printToStatus("Fetching Song information in background...");
                Thread thread = new Thread(new ReadIDTask(id, task));
                thread.setDaemon(true);
                thread.start();
            }

        });

        JFXButton closeButton = new JFXButton("CANCEL");
        closeButton.setOnAction(event -> alert.hideWithAnimation());

        layout.setActions(acceptButton, closeButton);

        alert.setContent(layout);
        alert.show();
    }

}
