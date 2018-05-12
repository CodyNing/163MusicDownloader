package ui;

import util.ElementNotFoundException;
import util.Playlist;
import util.Spider;

import java.io.IOException;

public interface DownloadEvent {

    void run(String id);

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

    class AlbumDownloadEvent implements DownloadEvent {
        @Override
        public void run(String id) {
            try {
                Spider.getAlbumByID(id).downloadAllSongs();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download album, id: %s\n", id));
                System.err.printf("Unable to download album, id: %s\n", id);
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get album, id: %s\n", id));
                e.printStackTrace();
            }
        }
    }

    class ArtistDownloadEvent implements DownloadEvent {
        @Override
        public void run(String id) {
            try {
                Spider.getArtistByID(id).downloadAllAlbum();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download artist's songs, id: %s\n", id));
                System.err.printf("Unable to download artist's songs, id: %s\n", id);
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get artist's songs, id: %s\n", id));
                e.printStackTrace();
            }
        }
    }

}
