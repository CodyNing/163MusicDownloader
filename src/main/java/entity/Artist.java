package entity;

import util.Database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.beans.property.SimpleStringProperty;

public class Artist extends Entity implements Serializable {

    private static final long serialVersionUID = 502L;

    private final String name;
    private final String id;
    private final Set<Album> albumList;
    private static List<String> columns = new ArrayList<>();
    
    static {
        columns.add("Artist Name");
        columns.add("Artist ID");
    }

    public Artist(String name) {
        this(name, null, new HashSet<>());
    }
    
    public Artist(String name, String id) {
        this(name, id, new HashSet<>());
    }

    public Artist(String name, String id, Set<Album> albumList) {
        this.name = name;
        this.id = id;
        this.albumList = albumList;
        
        setProperty();
        Database.addArtist(this);
    }

    public void addAlbum(Album album) {
        albumList.add(album);
    }

    public void downloadAllAlbum() {
        for (Album a : albumList) {
            a.downloadAllSongs();
        }
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Set<Album> getAlbumList() {
        return albumList;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", albumList=" + albumList +
                '}';
    }

    @Override
    public void setProperty() {
        properties.put("Artist Name", new SimpleStringProperty(name));
        properties.put("Artist ID", new SimpleStringProperty(id));
         
    }

    public static List<String> getColumns() {
        return columns;
    }

    public static void setColumns(List<String> columns) {
        Artist.columns = columns;
    }

}
