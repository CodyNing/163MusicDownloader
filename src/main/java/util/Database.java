package util;

import ui.Center;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Database implements Serializable {

    private static File SONG_DIR = new File("songs/");

    public static final File OUTPUT = new File("./database.ser");
    private static final long serialVersionUID = 500L;

    private static final Database database = init();
    private final Map<String, Song> songMap = new HashMap<>();
    private final Map<String, Artist> artistMap = new HashMap<>();
    private final Map<String, Album> albumMap = new HashMap<>();
    private final Map<String, Playlist> playlistMap = new HashMap<>();

    private int maxConcurrentDownload = 5;
    private int failConnectionWaitTime = 15;
    private int reconnectionTimes = 3;

    private Database() {
    }

    private static Database init() {
        if (OUTPUT.exists()) {
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(OUTPUT));
                Object obj = in.readObject();
                Center.printToStatus("Successfully read data from previous database...");
                return (Database) obj;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new Database();
    }

    public int getMaxConcurrentDownload() {
        return maxConcurrentDownload;
    }

    public void setMaxConcurrentDownload(int maxConcurrentDownload) {
        this.maxConcurrentDownload = maxConcurrentDownload;
    }

    public int getFailConnectionWaitTime() {
        return failConnectionWaitTime;
    }

    public void setFailConnectionWaitTime(int failConnectionWaitTime) {
        this.failConnectionWaitTime = failConnectionWaitTime;
    }

    public int getReconnectionTimes() {
        return reconnectionTimes;
    }

    public void setReconnectionTimes(int reconnectionTimes) {
        this.reconnectionTimes = reconnectionTimes;
    }

    public static Database getInstance() {
        return database;
    }

    public static Song getSong(String id) {
        return getInstance().songMap.get(id);
    }

    public static void addSong(Song song) {
        getInstance().songMap.putIfAbsent(song.getId(), song);
    }

    public static Artist getArtist(String id) {
        return getInstance().artistMap.get(id);
    }

    public static void addArtist(Artist artist) {
        getInstance().artistMap.putIfAbsent(artist.getId(), artist);
    }

    public static Album getAlbum(String id) {
        return getInstance().albumMap.get(id);
    }

    public static void addAlbum(Album album) {
        getInstance().albumMap.putIfAbsent(album.getId(), album);
    }

    public static Playlist getPlaylist(String id) {
        return getInstance().playlistMap.get(id);
    }

    public static void addPlaylist(Playlist playlist) {
        getInstance().playlistMap.putIfAbsent(playlist.getId(), playlist);
    }

    public static File getSongDir() {
        return SONG_DIR;
    }

    public static void setSongDir(File songDir) {
        // TODO set the Song Dir when no song is being download
    }
}
