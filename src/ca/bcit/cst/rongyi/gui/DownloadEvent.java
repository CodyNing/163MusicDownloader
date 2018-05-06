package ca.bcit.cst.rongyi.gui;

import ca.bcit.cst.rongyi.util.ElementNotFoundException;
import ca.bcit.cst.rongyi.util.Playlist;
import ca.bcit.cst.rongyi.util.Spider;

import java.io.IOException;

public interface DownloadEvent {
    void run(String id);

    class SongDownloadEvent implements DownloadEvent {
        @Override
        public void run(String id) {
            try {
                Spider.getSongByID(id).download();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download song, id: %s\n", id));
                System.err.printf("Unable to download song, id: %s\n", id);
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get song, id: %s\n", id));
                e.printStackTrace();
            }
        }
    }

    class PlaylistDownloadEvent implements DownloadEvent {

        @Override
        public void run(String id) {
            try {
                Playlist playlist = Spider.getPlaylistByID(id);
                playlist.downloadAllSongs();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to get playlist, id: %s\n", id));
                System.err.printf("Unable to get playlist, id: %s\n", id);
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get playlist, id: %s\n", id));
                e.printStackTrace();
            }
        }
    }

}
