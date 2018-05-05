package ca.bcit.cst.rongyi.gui;

import ca.bcit.cst.rongyi.util.Downloader;
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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    private static final File LOG_DIR = new File("./log/");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
//        registerErrorLog();
        VBox root = new VBox();
        root.setSpacing(10.0);
        root.setPadding(new Insets(10.0));

        Button playlistButton = new Button("Download by playlist id");
        playlistButton.setOnAction(e -> promptForPlaylistID());

        Button songButton = new Button("Download by song id");
        songButton.setOnAction(e -> promptForSongID());

        root.getChildren().addAll(playlistButton, songButton);

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("163 Music Downloader");
        primaryStage.show();

    }

    private void registerErrorLog() {
        if (!LOG_DIR.exists())
            LOG_DIR.mkdir();
        try {
            File logFile = new File(LOG_DIR.getAbsolutePath() + "\\error log - " + Downloader.makeStringValidForWindowsFile(LocalDateTime.now().toString()) + ".txt");
            logFile.createNewFile();
            System.setErr(new PrintStream(logFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void promptForPlaylistID() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter a id number");
        dialog.setContentText("All songs in the playlist will be downloaded");
        dialog.showAndWait().ifPresent(id -> {
            id = id.trim();
            if (!id.matches("^\\d*$")) {
                String regex = "playlist\\?id=(\\d*)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(id);
                if (matcher.find())
                    id = matcher.group(1);
            }
            String finalId = id;
            new Thread(() -> {
                try {
                    List<Song> songList = Spider.getSongByPlaylist(finalId);
                    for (Song song : songList) {
                        try {
                            song.download();
                        } catch (IOException e) {
                            System.err.printf("Unable to download song, %s\n", song);
                            e.printStackTrace();
                        }
                    }
                    System.out.printf("playlist id: %s, all songs added to download list\n", finalId);
                } catch (IOException e) {
                    System.out.printf("Unable to get playlist, id: %s\n", finalId);
                } catch (ElementNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    private void promptForSongID() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter a id number");
        dialog.setContentText("The Song will be downloaded");
        dialog.showAndWait().ifPresent(id -> {
            id = id.trim();
            if (!id.matches("^\\d*$")) {
                String regex = "song\\?id=(\\d*)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(id);
                if (matcher.find())
                    id = matcher.group(1);
            }
            String finalId = id;
            new Thread(() -> {
                try {
                    Spider.getSongByID(finalId).download();
                } catch (IOException e) {
                    System.out.printf("Unable to download song, id: %s\n", finalId);
                } catch (ElementNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

}
