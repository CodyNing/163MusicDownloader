package ui;

import util.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public interface DownloadEvent {

    boolean run(String id);

    class PlaylistDownloadEvent implements DownloadEvent {

        @Override
        public boolean run(String id) {
            try {
                Playlist playlist = Spider.getPlaylistByID(id);
                playlist.downloadAllSongs();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to get playlist, id: %s\n", id));
                System.err.printf("Unable to get playlist, id: %s\n", id);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get playlist, id: %s\n", id));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    class SongDownloadEvent implements DownloadEvent {
        @Override
        public boolean run(String id) {
            try {
                Spider.getSongByID(id).download();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download song, id: %s\n", id));
                System.err.printf("Unable to download song, id: %s\n", id);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get song, id: %s\n", id));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    class AlbumDownloadEvent implements DownloadEvent {
        @Override
        public boolean run(String id) {
            try {
                Spider.getAlbumByID(id).downloadAllSongs();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download album, id: %s\n", id));
                System.err.printf("Unable to download album, id: %s\n", id);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get album, id: %s\n", id));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    class ArtistDownloadEvent implements DownloadEvent {
        @Override
        public boolean run(String id) {
            try {
                Spider.getArtistByID(id).downloadAllAlbum();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download artist's songs, id: %s\n", id));
                System.err.printf("Unable to download artist's songs, id: %s\n", id);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get artist's songs, id: %s\n", id));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
    
    class PlaylistSearchEvent implements DownloadEvent {

        @Override
        public boolean run(String id) {
            try {
                Playlist playlist = Spider.getPlaylistByID(id);
                Searcher.setSearchList(playlist.getSongList());
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to get playlist, id: %s\n", id));
                System.err.printf("Unable to get playlist, id: %s\n", id);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get playlist, id: %s\n", id));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
    
    class SongSearchEvent implements DownloadEvent {
        @Override
        public boolean run(String id) {
            try {
                Set<Song> songlist = new HashSet<>();
                songlist.add(Spider.getSongByID(id));
                Searcher.setSearchList(songlist);
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download song, id: %s\n", id));
                System.err.printf("Unable to download song, id: %s\n", id);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get song, id: %s\n", id));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    class AlbumSearchEvent implements DownloadEvent {
        @Override
        public boolean run(String id) {
            try {
                Searcher.setSearchList(Spider.getAlbumByID(id).getSongList());
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download album, id: %s\n", id));
                System.err.printf("Unable to download album, id: %s\n", id);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get album, id: %s\n", id));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    class ArtistSearchEvent implements DownloadEvent {
        @Override
        public boolean run(String id) {
            try {
                Set<Album> albumlist = Spider.getArtistByID(id).getAlbumList();
                Set<Song> songlist = new HashSet<>();
                for(Album a : albumlist)
                    songlist.addAll(a.getSongList());
                Searcher.setSearchList(songlist);
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download artist's songs, id: %s\n", id));
                System.err.printf("Unable to download artist's songs, id: %s\n", id);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get artist's songs, id: %s\n", id));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}

