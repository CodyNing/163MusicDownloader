package ca.bcit.cst.rongyi.gui;

import ca.bcit.cst.rongyi.util.ElementNotFoundException;
import ca.bcit.cst.rongyi.util.Song;
import ca.bcit.cst.rongyi.util.Spider;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

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
            String finalId = id.trim();
            new Thread(() -> {
                try {
                    List<Song> songList = Spider.getSongByPlaylist(finalId);
                    for (Song song : songList) {
                        try {
                            song.download();
                        } catch (IOException e) {
                            System.out.printf("Unable to download song, %s\n", song);
                            e.printStackTrace();
                        }
                    }
                    System.out.printf("playlist id: %s, Download Complete", finalId);
                } catch (IOException e) {
                    System.out.printf("Unable to get playlist, id: %s\n", id);
                } catch (ElementNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    public void promptForSongID() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter a id number");
        dialog.setContentText("The Song will be downloaded");
        dialog.showAndWait().ifPresent(id -> {
            String finalId = id.trim();
            new Thread(() -> {
                try {
                    Spider.getSongByID(finalId).download();
                } catch (IOException e) {
                    System.out.printf("Unable to download song, id: %s\n", id);
                } catch (ElementNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

}
