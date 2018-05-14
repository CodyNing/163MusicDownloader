package entity;

import ui.Center;
import util.Database;
import util.Downloader;
import util.ElementNotFoundException;
import util.Spider;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

public class Playlist implements Serializable {

    private static final long serialVersionUID = 504L;

    private final String id;
    private final String title;
    private Set<Song> songList;

    public Playlist(String id, String title) throws IOException, ElementNotFoundException {
        this(id, title, Spider.getSongByPlaylist(id));
    }

    public Playlist(String id, String title, Set<Song> songList) {
        this.id = id;
        this.title = title;
        this.songList = songList;

        Database.addPlaylist(this);
    }

    public void downloadAllSongs() {
        Downloader.getInstance().downloadSong(songList);
        Center.printToStatus(String.format("playlist %s, all songs added to download list\n", title));
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Set<Song> getSongList() {
        return songList;
    }

    public void setSongList(Set<Song> songList) {
        this.songList = songList;
    }


    public int size() {
        return songList.size();
    }
}
