package entity;

import ui.Center;
import util.Database;
import util.Downloader;
import util.ElementNotFoundException;
import util.Spider;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javafx.beans.property.SimpleStringProperty;

public class Playlist extends Entity implements Serializable {

    private static final long serialVersionUID = 504L;

    private final String id;
    private final String title;
    private Set<Song> songList;
    private static List<String> columns = new ArrayList<>();

    static {
        columns.add("Playlist Title");
        columns.add("Playlist ID");
    }
    
    public Playlist(String id, String title) throws IOException, ElementNotFoundException {
        this(id, title, Spider.getSongByPlaylist(id));
    }

    public Playlist(String id, String title, Set<Song> songList) {
        this.id = id;
        this.title = title;
        this.songList = songList;

        setProperty();
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

    @Override
    public void setProperty() {
        properties.put("Playlist Title", new SimpleStringProperty(title));
        properties.put("Playlist ID", new SimpleStringProperty(id));
        
    }
    
    public static List<String> getColumns() {
        return columns;
    }

    public static void setColumns(List<String> columns) {
        Playlist.columns = columns;
    }
}
