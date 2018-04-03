package ca.bcit.cst.rongyi.util;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;

public class Song {

    private String id;
    private String title;
    private Artist artist;
    private Album album;
    private String downloadURL;

    public Song(String id, String title, Artist artist, Album album) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
    }

    public Song(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public void download(File dir) throws IOException {
        setDownloadURL();
        setArtistAndAlbum();
        File fp = Downloader.downloadSong(Song.this, dir);
        try {
            Downloader.setTag(this, fp);
        } catch (InvalidDataException | IOException | UnsupportedTagException | NotSupportedException e) {
            e.printStackTrace();
        }
    }

    private void setDownloadURL() {
        if (downloadURL != null)
            return;
        this.downloadURL = Spider.getSongDownloadURL(this.id);
    }

    public void setArtistAndAlbum() {
        if (artist != null && album != null)
            return;
        Spider.setArtistAndAlbum(this);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Artist getArtist() {
        return artist;
    }

    public Album getAlbum() {
        return album;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", artist=" + artist +
                ", album=" + album +
                ", downloadURL='" + downloadURL + '\'' +
                '}';
    }
}
