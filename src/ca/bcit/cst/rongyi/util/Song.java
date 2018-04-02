package ca.bcit.cst.rongyi.util;

public class Song {

    public final String id;
    public final String title;
    public final Artist artist;
    public final Album album;
    public final String downloadURL;

    public Song(String id, String title, Artist artist, Album album) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.downloadURL = Spider.getSongDownloadURL(id);
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
