package ca.bcit.cst.rongyi.util;

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
        makeTitleValid();
    }

    public Song(String id, String title) {
        this.id = id;
        this.title = title;
        makeTitleValid();
    }

    private void makeTitleValid() {
        this.title = this.title
                .replace(':', '：')
                .replace('<', '＜')
                .replace('>', '＞')
                .replace('\"', '＂')
                .replace('/', '／')
                .replace('\\', '｜')
                .replace('?', '？')
                .replace('*', '＊');
    }

    /**
     * Download the song with mp3 tags to the given directory.
     *
     * @param dir
     * @throws IOException
     */
    public void download(File dir) throws IOException {
        Downloader.getInstance().downloadSong(Song.this, dir);
    }

    public void download() throws IOException {
        download(Downloader.SONG_DIR);
    }

    public void setDownloadURL() {
        if (downloadURL != null) {
            return;
        }
        try {
            this.downloadURL = Spider.getSongDownloadURL(this.id);
        } catch (IOException e) {
            System.out.println("Failed to get Download URL From ouo.us");
            // TODO Try to read the url again (timeout, and max times to try)
            e.printStackTrace();
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
