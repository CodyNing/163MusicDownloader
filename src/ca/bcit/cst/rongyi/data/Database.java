package ca.bcit.cst.rongyi.data;

import ca.bcit.cst.rongyi.util.Album;
import ca.bcit.cst.rongyi.util.Artist;
import ca.bcit.cst.rongyi.util.Playlist;
import ca.bcit.cst.rongyi.util.Song;

import java.util.HashMap;
import java.util.List;

public class Database {

    private static HashMap<Playlist, List<Song>> playlistSongMap = new HashMap<>();
    private static HashMap<Song, Artist> songArtistHMap = new HashMap<>();
    private static HashMap<Song, Album> songAlbumMap = new HashMap<>();
    private static HashMap<Album, Artist> albumArtistMap = new HashMap<>();

}
