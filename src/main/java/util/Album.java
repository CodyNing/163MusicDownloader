package util;

import ui.Center;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Album implements Serializable {

    private static final long serialVersionUID = 503L;

    private final Artist artist;
    private final String name;
    private final String id;
    private final Set<Song> songList;

    public Album(Artist artist, String name, String id) {
        this(artist, name, id, new HashSet<>());
    }

    public Album(Artist artist, String name, String id, Set<Song> songList) {
        this.artist = artist;
        this.name = name;
        this.id = id;
        this.songList = songList;

        artist.addAlbum(this);
        Database.addAlbum(this);
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
