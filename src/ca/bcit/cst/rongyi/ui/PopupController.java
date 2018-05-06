package ca.bcit.cst.rongyi.ui;

import ca.bcit.cst.rongyi.util.Downloader;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;

import java.awt.*;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PopupController {

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
            desktop.open(Downloader.SONG_DIR);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
