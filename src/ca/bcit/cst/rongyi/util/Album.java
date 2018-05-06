package ca.bcit.cst.rongyi.util;

import java.util.ArrayList;
import java.util.List;

public class Album {

    private final Artist artist;
    private final String name;
    private final String id;
    private List<Song> songList = new ArrayList<>();

    public Album(Artist artist, String name, String id) {
        this.artist = artist;
        this.name = name;
        this.id = id;
    }

    public Album(Artist artist, String name, String id, List<Song> songList) {
        this.artist = artist;
        this.name = name;
        this.id = id;
        this.songList = songList;
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
