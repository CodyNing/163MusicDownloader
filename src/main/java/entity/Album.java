package entity;

import ui.Center;
import util.Database;
import util.Downloader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Album extends Entity implements Serializable {

    private static final long serialVersionUID = 503L;

    private final Artist artist;
    private final String name;
    private final String id;
    private final Set<Song> songList;
    private static List<String> columns = new ArrayList<>();

    static{
        columns.add("Album Name");
        columns.add("Album Artist");
        columns.add("Album ID");
    }
    
    public Album(Artist artist, String name, String id) {
        this(artist, name, id, new HashSet<>());
    }

    public Album(Artist artist, String name, String id, Set<Song> songList) {
        this.artist = artist;
        this.name = name;
        this.id = id;
        this.songList = songList;
        setProperty();

        artist.addAlbum(this);
        Database.addAlbum(this);
    }

    @Override
    public void setProperty() {
        properties.put("Album Name", new SimpleStringProperty(name));
        properties.put("Album ID", new SimpleStringProperty(id));
        StringProperty artistName = properties.get("artist");
        if (artistName == null && artist != null)
            properties.put("Album Artist", new SimpleStringProperty(artist.getName()));
    }

    public void addSong(Song song) {
        songList.add(song);
    }

    public void downloadAllSongs() {
        Downloader.getInstance().downloadSong(songList);
        Center.printToStatus(String.format("playlist id: %s, all songs added to download list\n", id));
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Artist getArtist() {
        return artist;
    }
    
    public Set<Song> getSongList() {
        return songList;
    }
    
    public static List<String> getColumns() {
        return columns;
    }

    public static void setColumns(List<String> columns) {
        Album.columns = columns;
    }

    @Override
    public String toString() {
        return "Album{" +
                "artist=" + artist +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", songList=" + songList +
                '}';
    }
}
