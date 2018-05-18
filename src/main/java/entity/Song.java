package entity;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ui.Center;
import util.Database;
import util.Downloader;
import util.Spider;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Song extends Entity implements Serializable {

    private static final long serialVersionUID = 501L;

    private final String id;
    private final String title;
    private String trackNo;
    private Artist artist;
    private Album album;
    private String downloadURL;
    private static List<String> columns = new ArrayList<>();

    static {
        columns.add("Song Title");
        columns.add("Song Artist");
        columns.add("Song Album");
        columns.add("Song ID");
    }
    
    public Song(String id, String title, String trackNo, Artist artist, Album album) {
        this.id = id;
        this.title = Downloader.makeStringValidForWindowsFile(title);
        this.trackNo = trackNo;
        setArtist(artist);
        setAlbum(album);

        properties.put("Song Title", new SimpleStringProperty(this.title));
        properties.put("Song ID", new SimpleStringProperty(this.id));

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
                System.err.printf("Failed to get Download URL, will try again in %s second, song: %s\n", data.getFailConnectionWaitTime(), getTitleProperty());
                try {
                    Thread.sleep(data.getFailConnectionWaitTime() * 1000);
                } catch (InterruptedException e1) {
                    // Let it go
                }
                setDownloadURL(tried + 1);
            } else {
                Center.printToStatus("Failed to get Download URL From ouo.us, give up, song: " + getTitleProperty());
                System.err.println("Failed to get Download URL From ouo.us, give up, song: " + getTitleProperty());
            }
        }
    }

    public void setArtistAndAlbum() {
        if (artist != null && album != null)
            return;
        try {
            Spider.setArtistAndAlbum(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setProperty() {
        setIDProperty();
        setTitleProperty();
        setArtistProperty();
        setAlbumProperty();
    }

    private void setIDProperty() {
        StringProperty IDProperty = properties.get("id");
        if (IDProperty == null)
            properties.put("Song ID", new SimpleStringProperty(id));
    }

    private void setTitleProperty() {
        StringProperty titleProperty = properties.get("title");
        if (titleProperty == null)
            properties.put("Song Title", new SimpleStringProperty(title));
    }

    private void setArtistProperty() {
        StringProperty artistName = properties.get("artist");
        if (artistName == null && artist != null)
            properties.put("Song Artist", new SimpleStringProperty(artist.getName()));
    }

    private void setAlbumProperty() {
        StringProperty albumName = properties.get("album");
        if (albumName == null && album != null)
            properties.put("Song Album", new SimpleStringProperty(album.getName()));
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleProperty() {
        return properties.get("Song Title").get();
    }

    public StringProperty titlePropertyProperty() {
        return properties.get("Song Title");
    }

    public StringProperty artistNameProperty() {
        return properties.get("Song Artist");
    }

    public StringProperty IDPropertyProperty() {
        return properties.get("Song ID");
    }

    public StringProperty albumNameProperty() {
        return properties.get("Song Album");


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
        if (artist == null)
            return;
        this.artist = artist;
        setArtistProperty();
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        if (album == null)
            return;
        this.album = album;
        album.addSong(this);
        setAlbumProperty();
    }

    public static List<String> getColumns() {
        return columns;
    }

    public static void setColumns(List<String> columns) {
        Song.columns = columns;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id='" + id + '\'' +
                ", titleProperty='" + getTitleProperty() + '\'' +
                ", artist=" + (artist != null ? artist.getName() : "null") +
                ", album=" + (album != null ? album.getName() : "null") +
                ", downloadURL='" + downloadURL + '\'' +
                '}';
    }

    public boolean exists() {
        return new File(Database.database.getSongDir() + "\\" + getArtist().getName() + " - " + getTitleProperty() + ".mp3").exists();
    }
}
