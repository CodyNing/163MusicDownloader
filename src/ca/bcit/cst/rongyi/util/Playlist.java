package ca.bcit.cst.rongyi.util;

import ca.bcit.cst.rongyi.gui.Center;

import java.io.IOException;
import java.util.List;

public class Playlist {

    private final String id;
    private final String title;
    private List<Song> songList;

    public Playlist(String id, String title) throws IOException, ElementNotFoundException {
        this.id = id;
        this.title = title;
        getPlaylist();
    }

    public Playlist(String id, String title, List<Song> songList) {
        this.id = id;
        this.title = title;
        this.songList = songList;
    }

    private void getPlaylist() throws IOException, ElementNotFoundException {
        this.songList = Spider.getSongByPlaylist(id);
    }

    public void downloadAllSongs() {
        for (Song song : songList) {
            try {
                song.download();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download song, %s\n", song));
                System.err.printf("Unable to download song, %s\n", song);
                e.printStackTrace();
            }
        }
        Center.printToStatus(String.format("playlist id: %s, all songs added to download list\n", id));
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }


}
