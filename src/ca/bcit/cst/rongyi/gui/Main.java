package ca.bcit.cst.rongyi.gui;

import ca.bcit.cst.rongyi.util.Database;
import ca.bcit.cst.rongyi.util.Downloader;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: MenuBar: Setting...
 */
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
        Database.getInstance();

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
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(Database.OUTPUT));
                out.writeObject(Database.getInstance());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        primaryStage.show();
    }

    private void promptDialog(DownloadEvent task, String tag, String promptMsg) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter a id number");
        dialog.setContentText(promptMsg);

        dialog.showAndWait().ifPresent(id -> {
            // use regex to fetch song id from url if necessary
            id = id.trim();
            if (!id.matches("^\\d*$")) {
                String regex = tag + "\\?id=(\\d*)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(id);
                if (matcher.find())
                    id = matcher.group(1);
            }
            // return if the id is not a number
            if (!id.matches("^\\d*$")) {
                Center.printToStatus(String.format("id is not a number, id: %s\n", id));
                return;
            }
            // Start a new Thread to download the song in background
            new Thread(new ReadIDTask(id, task)).start();
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
            playlistButton.setOnAction(e -> promptDialog(new DownloadEvent.PlaylistDownloadEvent(), "playlist", "All songs in the playlist will be downloaded"));

            Button songButton = new Button("Single Song");
            songButton.setOnAction(e -> promptDialog(new DownloadEvent.SongDownloadEvent(), "song", "The Song will be downloaded"));

            Button openFolderButton = new Button("Open Containing Folder");
            openFolderButton.setOnAction(event -> {
                try {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(Downloader.SONG_DIR);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            toolBar.getItems().addAll(playlistButton, songButton, openFolderButton);

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
