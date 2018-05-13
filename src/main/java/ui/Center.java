package ui;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import util.Database;
import util.Downloader;
import util.Song;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static JFXTreeTableView<Song> searchView;

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

    public static void setUpIdValidationTextField(String tag, JFXTextField textField) {
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

    public static void setSearchView(JFXTreeTableView<Song> searchView) {
        Center.searchView = searchView;
    }

    public static void setSearchList(Set<Song> searchList) {
        Platform.runLater(() -> {
            ObservableList<Song> dataList = FXCollections.observableArrayList(searchList);
            searchView.setRoot(new RecursiveTreeItem<>(dataList, RecursiveTreeObject::getChildren));
            for (Song song : dataList) {
                song.setProperty();
            }
            Thread thread = new Thread(() -> {
                for (Song song : dataList) {
                    song.setArtistAndAlbum();
                }
            });
            thread.setDaemon(true);
            thread.start();
        });
    }

}
