package entity;

import util.Database;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

public class Artist extends RecursiveTreeObject<Artist> implements Serializable {

    private static final long serialVersionUID = 502L;

    private final String name;
    private final String id;
    private final Set<Album> albumList;

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

}
