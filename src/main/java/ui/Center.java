package ui;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import entity.Song;
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
import util.ThreadUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Center {

    private static Label statusLabel;

    private static final List<Runnable> closeEventList = new ArrayList<>();

    private static File newSongDir;

    public static final EventHandler<WindowEvent> CLOSE_EVENT = (EventHandler<WindowEvent>) event -> {
        // set to new directory
        if (newSongDir != null) {
            for (File file : Database.database.getSongDir().listFiles()) {
                try {
                    Files.move(file.toPath(), Paths.get(newSongDir.getAbsolutePath() + "\\" + file.getName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Database.database.setSongDir(newSongDir);
        }
        // delete temp files
        for (File f : Downloader.TEMP_DIR.listFiles()) {
            f.delete();
        }
        // Save data
        closeEventList.add(() -> {
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(Database.OUTPUT));
                out.writeObject(Database.getInstance());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        for (Runnable runnable : closeEventList) {
            runnable.run();
        }
    };
    private static Scene rootScene;
    private static JFXTreeTableView<Song> searchView;
    private static Label searchListLabel;

    public static void setLabel(Label statusLabel) {
        Center.statusLabel = statusLabel;
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
    
    public static void setUpKeywordTextField(JFXTextField textField) {
        textField.getValidators().clear();
//        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
//            // use regex to fetch song id from url if necessary
//            String id = textField.getText().trim();
//            if (!id.matches("^\\d*$")) {
//                String regex = tag + "\\?id=(\\d*)";
//                Pattern pattern = Pattern.compile(regex);
//                Matcher matcher = pattern.matcher(id);
//                if (matcher.find())
//                    id = matcher.group(1);
//            }
//            textField.setText(id);
//
//            textField.validate();
//        });
        textField.setPromptText("Searching by Keyword");
        textField.setLabelFloat(true);
    }

    public static void printToStatus(String status) {
        Platform.runLater(() -> statusLabel.setText(status));
    }

    public static Window getRootWindow() {
        return rootScene.getWindow();
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
            searchListLabel.setText(String.format("Found %s results", searchList.size()));
            for (Song song : dataList) {
                song.setProperty();
            }
            ThreadUtils.startNormalThread(() -> {
                for (Song song : dataList) {
                    song.setArtistAndAlbum();
                }
            });
        });
    }

    public static void addCloseEvent(Runnable runnable) {
        closeEventList.add(runnable);
    }

    public static void setSearchListLabel(Label searchListLabel) {
        Center.searchListLabel = searchListLabel;
    }

    public static void setNewSongDir(File newSongDir) {
        Center.newSongDir = newSongDir;
    }
}
