package ca.bcit.cst.rongyi.gui;

import ca.bcit.cst.rongyi.util.Downloader;
import ca.bcit.cst.rongyi.util.ElementNotFoundException;
import ca.bcit.cst.rongyi.util.Song;
import ca.bcit.cst.rongyi.util.Spider;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Priority;
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

    public static final double WIDTH = 800;
    public static final double HEIGHT = 600;

    private static final File LOG_DIR = new File("./log/");
    private final UIUtility util = new UIUtility();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
//        registerErrorLog();
        VBox root = new VBox();
        root.setSpacing(10.0);
        root.setPadding(new Insets(10.0));

        StatusBar statusBar = util.statusBar();
        ListView<Downloader.Download> listView = util.downloadListView();
        VBox.setVgrow(listView, Priority.ALWAYS);
        Center.setStatusBar(statusBar);
        root.getChildren().addAll(util.buttonBar(), listView, statusBar);

        primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));
        primaryStage.setTitle("163 Music Downloader");

        primaryStage.setOnCloseRequest(event -> {
            for (File f : Downloader.TEMP_DIR.listFiles()) {
                f.delete();
            }
        });

        primaryStage.show();
    }

    private void promptForPlaylistID() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter a id number");
        dialog.setContentText("All songs in the playlist will be downloaded");

        dialog.showAndWait().ifPresent(id -> {
            // use regex to fetch playlist id from url if necessary
            id = id.trim();
            if (!id.matches("^\\d*$")) {
                String regex = "playlist\\?id=(\\d*)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(id);
                if (matcher.find())
                    id = matcher.group(1);
            }
            String finalId = id;
            // start a Thread to start download in background
            new Thread(new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        List<Song> songList = Spider.getSongByPlaylist(finalId);
                        for (Song song : songList) {
                            try {
                                song.download();
                            } catch (IOException e) {
                                Center.printToStatus(String.format("Unable to download song, %s\n", song));
                                System.err.printf("Unable to download song, %s\n", song);
                                e.printStackTrace();
                            }
                        }
                        Center.printToStatus(String.format("playlist id: %s, all songs added to download list\n", finalId));
                    } catch (IOException e) {
                        Center.printToStatus(String.format("Unable to get playlist, id: %s\n", finalId));
                        System.err.printf("Unable to get playlist, id: %s\n", finalId);
                    } catch (ElementNotFoundException e) {
                        Center.printToStatus(String.format("Unable to get playlist, id: %s\n", finalId));
                        e.printStackTrace();
                    }
                    return null;
                }
            }).start();
        });
    }

    private void promptForSongID() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter a id number");
        dialog.setContentText("The Song will be downloaded");

        dialog.showAndWait().ifPresent(id -> {
            // use regex to fetch song id from url if necessary
            id = id.trim();
            if (!id.matches("^\\d*$")) {
                String regex = "song\\?id=(\\d*)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(id);
                if (matcher.find())
                    id = matcher.group(1);
            }
            String finalId = id;
            // Start a new Thread to download the song in background
            new Thread(new Task<Void>() {


                @Override
                protected Void call() throws Exception {
                    try {
                        Spider.getSongByID(finalId).download();
                    } catch (IOException e) {
                        Center.printToStatus(String.format("Unable to download song, id: %s\n", finalId));
                        System.err.printf("Unable to download song, id: %s\n", finalId);
                    } catch (ElementNotFoundException e) {
                        Center.printToStatus(String.format("Unable to get song, id: %s\n", finalId));
                        e.printStackTrace();
                    }
                    return null;
                }
            }).start();
        });
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

    private class UIUtility {

        private ToolBar buttonBar() {
            ToolBar toolBar = new ToolBar();
            toolBar.setPadding(new Insets(5.0));

            Button playlistButton = new Button("Playlist");
            playlistButton.setOnAction(e -> promptForPlaylistID());

            Button songButton = new Button("Single Song");
            songButton.setOnAction(e -> promptForSongID());

            toolBar.getItems().addAll(playlistButton, songButton);

            return toolBar;
        }

        private ListView<Downloader.Download> downloadListView() {
            ListView<Downloader.Download> downloadListView = new ListView<>(Downloader.getInstance().getDownloadList());
            downloadListView.setCellFactory((ListView<Downloader.Download> l) -> new DownloadCell());
            return downloadListView;
        }

        private StatusBar statusBar() {
            return new StatusBar();
        }

    }

}
