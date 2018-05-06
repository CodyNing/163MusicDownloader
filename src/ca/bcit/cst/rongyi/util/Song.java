package ca.bcit.cst.rongyi.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class Song implements Serializable {

    private static final long serialVersionUID = 501L;

    private final String id;
    private final String title;
    private Artist artist;
    private Album album;
    private String downloadURL;

    public Song(String id, String title, Artist artist, Album album) {
        this.id = id;
        this.title = Downloader.makeStringValidForWindowsFile(title);
        this.artist = artist;
        this.album = album;

        if (album != null)
            album.addSong(this);
        Database.addSong(this);
    }

    public Song(String id, String title) {
        this(id, title, null, null);
    }

    /**
     * Download the song with mp3 tags to the given directory.
     *
     * @param dir directory
     */
    public void download(File dir) {
        Downloader.getInstance().downloadSong(Song.this, dir);
    }

    public void download() {
        download(Downloader.TEMP_DIR);
    }

    public void setDownloadURL() {
        setDownloadURL(0);
    }

    private void setDownloadURL(int tried) {
        if (downloadURL != null) {
            return;
        }
        try {
            this.downloadURL = Spider.getSongDownloadURL(this.id);
        } catch (IOException e) {
            if (tried < 3) {
                System.err.println("Failed to get Download URL, will try again in 15 second, song: " + getTitle());
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                setDownloadURL(tried + 1);
            } else {
                System.err.println("Failed to get Download URL From ouo.us, give up, song: " + getTitle());
            }
        }
    }

    public void setArtistAndAlbum() {
        if (artist != null && album != null)
            return;
        try {
            Spider.setArtistAndAlbum(this);
        } catch (ElementNotFoundException e) {
            e.printStackTrace();
        }
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

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
        album.addSong(this);
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", artist=" + (artist != null ? artist.getName() : "null") +
                ", album=" + (album != null ? album.getName() : "null") +
                ", downloadURL='" + downloadURL + '\'' +
                '}';
    }

    public boolean exists() {
        return new File(Downloader.SONG_DIR + "\\" + getArtist().getName() + " - " + getTitle() + ".mp3").exists();
    }
}
