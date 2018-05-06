package ca.bcit.cst.rongyi.util;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Artist implements Serializable {

    private static final long serialVersionUID = 502L;

    private final String name;
    private final String id;
    private Set<Album> albumList;

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
