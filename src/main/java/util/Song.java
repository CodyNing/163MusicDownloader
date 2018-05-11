package util;

import ui.Center;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class Song implements Serializable {

    private static final long serialVersionUID = 501L;

    private final String id;
    private final String title;
    private String trackNo;
    private Artist artist;
    private Album album;
    private String downloadURL;

    public Song(String id, String title, String trackNo, Artist artist, Album album) {
        this.id = id;
        this.title = Downloader.makeStringValidForWindowsFile(title);
        this.trackNo = trackNo;
        this.artist = artist;
        this.album = album;

        if (album != null)
            album.addSong(this);
        Database.addSong(this);
    }
    
    public Song(String id, String title, Artist artist, Album album) {
        this(id, title, null, artist, album);
    }

    public Song(String id, String title) {
        this(id, title, null, null, null);
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
            Database data = Database.getInstance();
            if (tried < data.getReconnectionTimes()) {
                System.err.printf("Failed to get Download URL, will try again in %s second, song: %s\n", data.getFailConnectionWaitTime(), getTitle());
                try {
                    Thread.sleep(data.getFailConnectionWaitTime() * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                setDownloadURL(tried + 1);
            } else {
                Center.printToStatus("Failed to get Download URL From ouo.us, give up, song: " + getTitle());
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
    
    public String getTrackNo() {
        return trackNo;
    }

    public void setTrackNo(String trackNo) {
        this.trackNo = trackNo;
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
        return new File(Database.getSongDir() + "\\" + getArtist().getName() + " - " + getTitle() + ".mp3").exists();
    }
}
