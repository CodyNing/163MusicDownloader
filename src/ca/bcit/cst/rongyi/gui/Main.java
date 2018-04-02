package ca.bcit.cst.rongyi.gui;

import ca.bcit.cst.rongyi.util.Downloader;
import ca.bcit.cst.rongyi.util.Spider;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();
        root.setSpacing(10.0);
        root.setPadding(new Insets(10.0));

        Button playlistButton = new Button("Download by playlist id");
        playlistButton.setOnAction(e -> {
            promptForPlaylistID();
        });

        Button songButton = new Button("Download by song id");
        songButton.setOnAction(e -> {
            promptForSongID();
        });

        root.getChildren().addAll(playlistButton, songButton);

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("163 Music Downloader");
        primaryStage.show();
    }

    public void promptForPlaylistID() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter a id number");
        dialog.setContentText("All songs in the playlist will be downloaded");
        dialog.showAndWait().ifPresent(id -> {
            id = id.trim();
            File dir = chooseDirectory();
            Spider.getSongByPlaylist(id).forEach(song ->
                    Downloader.downloadSong(song, dir)
            );
        });
    }

    public void promptForSongID() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter a id number");
        dialog.setContentText("The Song will be downloaded");
        dialog.showAndWait().ifPresent(id -> {
            id = id.trim();
            File dir = chooseDirectory();
            Downloader.downloadSong(Spider.getSongByID(id), dir);
        });
    }

    public File chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Set the directory to save song files");
        directoryChooser.setInitialDirectory(new File("./songs/"));
        File dir = directoryChooser.showDialog(null);
        return dir;
    }
}
