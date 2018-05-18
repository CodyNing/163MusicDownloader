package ui;

import entity.Album;
import entity.Artist;
import entity.Playlist;
import entity.Song;
import javafx.application.Platform;
import util.ElementNotFoundException;
import util.Spider;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public interface RunnableEvent {

    boolean run(String id);

    class PlaylistDownloadEvent implements RunnableEvent {

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

    class SongDownloadEvent implements RunnableEvent {
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

    class AlbumDownloadEvent implements RunnableEvent {
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

    class ArtistDownloadEvent implements RunnableEvent {
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

    class PlaylistSearchEvent implements RunnableEvent {

        @Override
        public boolean run(String id) {
            try {
                Platform.runLater(() -> {
                    MainController.main.setupViewColumns(Song.getColumns());
                    Center.setSongCellMenu();
                });
                Playlist playlist = Spider.getPlaylistByID(id);
                Center.setSearchList(playlist.getSongList());
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

    class SongSearchEvent implements RunnableEvent {
        @Override
        public boolean run(String id) {
            try {
                Platform.runLater(() -> {
                    MainController.main.setupViewColumns(Song.getColumns());
                    Center.setSongCellMenu();
                });
                Song song = Spider.getSongByID(id);
                Set<Song> songList = new HashSet<>();
                songList.add(song);
                Center.setSearchList(songList);
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

    class AlbumSearchEvent implements RunnableEvent {
        @Override
        public boolean run(String id) {
            try {
                Platform.runLater(() -> {
                    MainController.main.setupViewColumns(Song.getColumns());
                    Center.setSongCellMenu();
                });
                Center.setSearchList(Spider.getAlbumByID(id).getSongList());
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

    class ArtistSearchEvent implements RunnableEvent {
        @Override
        public boolean run(String id) {
            try {
                Platform.runLater(() -> {
                    MainController.main.setupViewColumns(Album.getColumns());
                    Center.setAlbumCellDC();
                });
                Set<Album> albumList = Spider.getArtistByID(id).getAlbumList();
                Center.setSearchList(albumList);
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
    
    class SongKWSearchEvent implements RunnableEvent {
        @Override
        public boolean run(String keyword) {
            try {
                Platform.runLater(() -> {
                    MainController.main.setupViewColumns(Song.getColumns());
                    Center.setSongCellMenu();
                });
                Center.setSearchList(Spider.getSongByStringSearch(keyword));
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download artist's songs, id: %s\n", keyword));
                System.err.printf("Unable to download artist's songs, id: %s\n", keyword);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get artist's songs, id: %s\n", keyword));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
    
    class ArtistKWSearchEvent implements RunnableEvent {
        @Override
        public boolean run(String keyword) {
            try {
                Platform.runLater(() -> {
                    MainController.main.setupViewColumns(Artist.getColumns());
                    Center.setArtistCellDC();
                });
                Center.setSearchList(Spider.getArtistByStringSearch(keyword));
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download artist's songs, id: %s\n", keyword));
                System.err.printf("Unable to download artist's songs, id: %s\n", keyword);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get artist's songs, id: %s\n", keyword));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
    
    class AlbumKWSearchEvent implements RunnableEvent {
        @Override
        public boolean run(String keyword) {
            try {
                Platform.runLater(() -> {
                        MainController.main.setupViewColumns(Album.getColumns());
                        Center.setAlbumCellDC();
                    });
                Center.setSearchList(Spider.getAlbumByStringSearch(keyword));
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download artist's songs, id: %s\n", keyword));
                System.err.printf("Unable to download artist's songs, id: %s\n", keyword);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get artist's songs, id: %s\n", keyword));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
    
    class PlaylistKWSearchEvent implements RunnableEvent {
        @Override
        public boolean run(String keyword) {
            try {
                Platform.runLater(() -> {
                    MainController.main.setupViewColumns(Playlist.getColumns());
                    Center.setPlaylistCellDC();
                });
                Center.setSearchList(Spider.getPlaylistByStringSearch(keyword));
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download artist's songs, id: %s\n", keyword));
                System.err.printf("Unable to download artist's songs, id: %s\n", keyword);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get artist's songs, id: %s\n", keyword));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}

