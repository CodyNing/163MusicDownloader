package ui;

import util.Database;
import util.Downloader;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Center {

    private static Label downloadStatus;

    private static Label statusLabel;

    public static final EventHandler<WindowEvent> CLOSE_EVENT = (EventHandler<WindowEvent>) event -> {
        for (File f : Downloader.TEMP_DIR.listFiles()) {
            f.delete();
        }
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(Database.OUTPUT));
            out.writeObject(Database.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
    private static Scene rootScene;

    public static void setLabel(Label downloadStatus, Label statusLabel) {
        Center.downloadStatus = downloadStatus;
        Center.statusLabel = statusLabel;
    }

    public static void updateListStatus() {
        Platform.runLater(() -> {
            Downloader downloader = Downloader.getInstance();
            int downloading = downloader.getCurrentDownloading();
            int size = downloader.getDownloadList().size();

            String status = String.format(" %s / %s downloading", downloading, size);
            downloadStatus.setText(status);
        });
    }

    public static void printToStatus(String status) {
        Platform.runLater(() -> statusLabel.setText(status));
    }

    public static Window getRootWindow() {
        return rootScene.getWindow();
    }

    public static Scene getRootScene() {
        return rootScene;
    }

    public static void setRootScene(Scene rootScene) {
        Center.rootScene = rootScene;
    }
}
