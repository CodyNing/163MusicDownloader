package util;

import entity.Album;
import entity.Artist;
import entity.Playlist;
import entity.Song;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import ui.Center;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Spider {

    private static final String PLAYLIST_URL = "http://music.163.com/playlist";
    private static final String SONG_URL = "http://music.163.com/song";
    private static final String ALBUM_URL = "http://music.163.com/album";
    private static final String ARTIST_URL = "http://music.163.com/artist/album";
    private static final String DOWNLOADER_URL = "https://ouo.us/fm/163/";
    private static final String SEARCH_URL = "http://music.163.com/weapi/search/get";
    private static final String SEARCH_TYPE_SONG = "1";
    private static final String SEARCH_TYPE_ARTIST = "100";
    private static final String SEARCH_TYPE_ALBUM = "10";
    private static final String SEARCH_TYPE_PLAYLIST = "1000";

    /* The number of albums to display in one page, set to 1000 because want all albums at once */
    private static final String DISPLAY_LIMIT = "1000";

    private static Connection get163Connection(String url) {
        return Jsoup.connect(url)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Encoding", "gzip, deflate, sdch")
                .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6")
                .header("Cache-Control", "no-cache")
                .header("Connection", "keep-alive")
                .header("Cookie", "_ntes_nnid=7eced19b27ffae35dad3f8f2bf5885cd,1476521011210; _ntes_nuid=7eced19b27ffae35dad3f8f2bf5885cd; usertrack=c+5+hlgB7TgnsAmACnXtAg==; Province=025; City=025; _ga=GA1.2.1405085820.1476521280; NTES_PASSPORT=6n9ihXhbWKPi8yAqG.i2kETSCRa.ug06Txh8EMrrRsliVQXFV_orx5HffqhQjuGHkNQrLOIRLLotGohL9s10wcYSPiQfI2wiPacKlJ3nYAXgM; P_INFO=hourui93@163.com|1476523293|1|study|11&12|jis&1476511733&mail163#jis&320100#10#0#0|151889&0|g37_client_check&mailsettings&mail163&study&blog|hourui93@163.com; JSESSIONID-WYYY=189f31767098c3bd9d03d9b968c065daf43cbd4c1596732e4dcb471beafe2bf0605b85e969f92600064a977e0b64a24f0af7894ca898b696bd58ad5f39c8fce821ec2f81f826ea967215de4d10469e9bd672e75d25f116a9d309d360582a79620b250625859bc039161c78ab125a1e9bf5d291f6d4e4da30574ccd6bbab70b710e3f358f%3A1476594130342; _iuqxldmzr_=25; __utma=94650624.1038096298.1476521011.1476588849.1476592408.6; __utmb=94650624.11.10.1476592408; __utmc=94650624; __utmz=94650624.1476521011.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)")
                .header("DNT", "1")
                .header("Host", "music.163.com")
                .header("Pragma", "no-cache")
                .header("Referer", "http,//music.163.com/")
                .header("Upgrade-Insecure-Requests", "1")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");
    }
    
    public static Connection.Response getSearchResult(String keyword, String type) throws IOException {
        JSONObject json = SearchMusicList(keyword, type);
        String req_str = json.toJSONString();
        Connection.Response
                response = Jsoup.connect(SEARCH_URL)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:57.0) Gecko/20100101 Firefox/57.0")
                .header("Accept", "*/*")
                .header("Cache-Control", "no-cache")
                .header("Connection", "keep-alive")
                .header("Host", "music.163.com")
                .header("Accept-Language", "zh-CN,en-US;q=0.7,en;q=0.3")
                .header("DNT", "1")
                .header("Pragma", "no-cache")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .data(EncryptUtils.encrypt(req_str))
                .method(Connection.Method.POST)
                .ignoreContentType(true)
                .timeout(10000)
                .execute();
        return response;
    }
    
    public static JSONObject SearchMusicList(String s,String type) {
        JSONObject json = new JSONObject();
        json.put("s", s);
        json.put("type",type);
        json.put("offset", 0);
        json.put("total", "True");
        json.put("limit", 50);

        return json;
    }
    
    public static String getSongDownloadURL(String songID) throws IOException {
        Element body = get163Connection(DOWNLOADER_URL)
                .data("id", songID)
                .get().body();

        return body.select("a[class=button]").get(1).attr("href");
    }

    public static List<Song> getSongs(List<String> songIDList) throws IOException, ElementNotFoundException {
        List<Song> songList = new ArrayList<>();
        for (String songID : songIDList) {
            songList.add(getSongByID(songID));
        }

        return songList;
    }

    public static Set<Song> getSongByPlaylist(String playlistId) throws IOException, ElementNotFoundException {
        Playlist playlist;
        if ((playlist = Database.getPlaylist(playlistId)) != null)
            return playlist.getSongList();

        Set<Song> songIDList = new HashSet<>();

        Element body = get163Connection(PLAYLIST_URL)
                .data("id", playlistId)
                .get().body();

        Element eleListDetail = body.selectFirst("ul[class=f-hide]");
        if (eleListDetail == null)
            throw new ElementNotFoundException("invalid playlist id, id: " + playlistId);
        Elements eleSongList = eleListDetail.select("a[href]");
        if (eleSongList.size() == 0)
            throw new ElementNotFoundException("Unable to get playlist, id: " + playlistId);
        eleSongList.forEach(song -> songIDList.add(new Song(song.attr("href").substring(9), song.text()))
        );

        return songIDList;
    }

    public static Playlist getPlaylistByID(String playlistId) throws IOException, ElementNotFoundException {
        Playlist playlist;
        if ((playlist = Database.getPlaylist(playlistId)) != null)
            return playlist;

        Set<Song> songList = new HashSet<>();
        Element body = get163Connection(PLAYLIST_URL)
                .data("id", playlistId)
                .get().body();

        Element elePlaylistTitle = body.selectFirst("h2[class=f-ff2 f-brk]");
        if (elePlaylistTitle == null)
            throw new ElementNotFoundException("cannot find playlist title, id: " + playlistId);
        Element eleListDetail = body.selectFirst("ul[class=f-hide]");
        if (eleListDetail == null)
            throw new ElementNotFoundException("invalid playlist id, id: " + playlistId);
        Elements eleSongList = eleListDetail.select("a[href]");
        if (eleSongList.size() == 0)
            throw new ElementNotFoundException("Unable to get playlist, id: " + playlistId);
        eleSongList.forEach(song -> songList.add(new Song(song.attr("href").substring(9), song.text())));

        return new Playlist(playlistId, elePlaylistTitle.text(), songList);
    }

    public static Album getAlbumByID(String albumID) throws IOException, ElementNotFoundException {
        Album album;
        if ((album = Database.getAlbum(albumID)) != null)
            return album;

        Set<Song> songList = new HashSet<>();
        Element body = get163Connection(ALBUM_URL)
                .data("id", albumID)
                .get().body();

        Element eleAlbumTitle = body.selectFirst("div[class=tit]").selectFirst("h2[class=f-ff2]");
        if (eleAlbumTitle == null)
            throw new ElementNotFoundException("cannot find album title, id: " + albumID);

        Element eleArtist = body.selectFirst("p[class=intr]").selectFirst("a[class=s-fc7]");
        if (eleArtist == null)
            throw new ElementNotFoundException("cannot find artist, id: " + albumID);
        Artist artist = new Artist(eleArtist.text(), eleArtist.attr("href").substring(11));

        Element eleListDetail = body.selectFirst("ul[class=f-hide]");
        if (eleListDetail == null)
            throw new ElementNotFoundException("invalid playlist id, id: " + albumID);
        Elements eleSongList = eleListDetail.select("a[href]");
        if (eleSongList.size() == 0)
            throw new ElementNotFoundException("Unable to get playlist, id: " + albumID);

        album = new Album(artist, eleAlbumTitle.text(), albumID, songList);
        Album finalAlbum = album;
        int trackNo = 0;
        for (Element song : eleSongList) {
            Song temp = new Song(song.attr("href").substring(9), song.text());
            temp.setTrackNo(++trackNo + "");
            temp.setArtist(artist);
            temp.setAlbum(finalAlbum);
            songList.add(temp);
        }
        return album;
    }

    public static Song getSongByID(String songId) throws IOException, ElementNotFoundException {
        Song song;
        if ((song = Database.getSong(songId)) != null)
            return song;

        Element body = get163Connection(SONG_URL)
                .data("id", songId)
                .get().body();

        Element info = body.selectFirst("div[class=cnt]");
        if (info == null)
            throw new ElementNotFoundException("Unable to get song, id: " + songId);
        String songTitle = info.selectFirst("em[class=f-ff2]").text();
        Elements eleInfo = info.select("a[class=s-fc7]");
        Element eleArtist = eleInfo.get(0);
        Artist artist = new Artist(eleArtist.text(), eleArtist.attr("href").substring(11));
        Element eleAlbum = eleInfo.get(1);
        Album album = new Album(artist, eleAlbum.text(), eleAlbum.attr("href").substring(10));

        return new Song(songId, songTitle, artist, album);
    }

    public static Artist getArtistByID(String artistID) throws IOException, ElementNotFoundException {
        Artist artist;
        Element body = get163Connection(ARTIST_URL)
                .data("id", artistID)
                .data("limit", DISPLAY_LIMIT)
                .get().body();

        Element artistName = body.selectFirst("h2[id=artist-name]");
        if (artistName == null)
            throw new ElementNotFoundException("Unable to get artist, id: " + artistID);

        Set<Album> albumSet = new HashSet<>();
        Elements eleInfo = body.selectFirst("ul[class=m-cvrlst m-cvrlst-alb4 f-cb]").select("a[class=icon-play f-alpha]");
        if (eleInfo == null)
            throw new ElementNotFoundException("Unable to get albums, id: " + artistID);
        for (Element e : eleInfo) {
            String albumID = e.attr("data-res-id");
            albumSet.add(getAlbumByID(albumID));
        }

        artist = new Artist(artistName.text(), artistID, albumSet);

        Center.printToStatus("retrieved data for artist " + artist.getName());

        return artist;
    }

    public static void setArtistAndAlbum(Song song) throws ElementNotFoundException {
        try {
            Element body = get163Connection(SONG_URL)
                    .data("id", song.getId())
                    .get().body();

            Element info = body.selectFirst("div[class=cnt]");
            if (info == null)
                throw new ElementNotFoundException("Unable to get song : " + song);
            Elements eleInfo = info.select("a[class=s-fc7]");
            if (eleInfo.size() < 2)
                return;
            Element eleArtist = eleInfo.get(0);
            Artist artist = new Artist(eleArtist.text(), eleArtist.attr("href").substring(11));
            Element eleAlbum = eleInfo.get(1);
            Album album = new Album(artist, eleAlbum.text(), eleAlbum.attr("href").substring(10));
            song.setArtist(artist);
            song.setAlbum(album);
        } catch (IOException e) {
            System.err.printf("Cannot get Artist and Album from song, id: %s\n", song.getId());
        }
    }

    public static Set<Song> getSongByStringSearch(String keyword) throws IOException, ElementNotFoundException{
        Set<Song> songSearchResult = new HashSet<>();
        Connection.Response res = getSearchResult(keyword, SEARCH_TYPE_SONG);
        JSONObject json = (JSONObject) JSON.parse(res.body());
        JSONArray songjson = json.getJSONObject("result").getJSONArray("songs");
        if(songjson.size() == 0)
            throw new ElementNotFoundException();
        for(int i = 0; i < songjson.size(); i++) {
            JSONObject songinfo = songjson.getJSONObject(i);
            JSONObject albuminfo = songinfo.getJSONObject("album");
            JSONObject artistinfo = songinfo.getJSONArray("artists").getJSONObject(0);
            Artist artist = new Artist(artistinfo.getString("name"), artistinfo.get("id").toString());
            Album album =  new Album(artist, albuminfo.getString("name"), albuminfo.get("id").toString());
            Song song = new Song(songinfo.get("id").toString(), songinfo.getString("name"), artist, album);
            songSearchResult.add(song);
        }
        return songSearchResult;
    }
    
    public static Set<Artist> getArtistByStringSearch(String keyword) throws IOException, ElementNotFoundException{
        Set<Artist> artistSearchResult = new HashSet<>();
        Connection.Response res = getSearchResult(keyword, SEARCH_TYPE_ARTIST);
        JSONObject json = (JSONObject) JSON.parse(res.body());
        JSONArray artistjson = json.getJSONObject("result").getJSONArray("artists");
        if(artistjson.size() == 0)
            throw new ElementNotFoundException();
        for(int i = 0; i < artistjson.size(); i++) {
            JSONObject artistinfo = artistjson.getJSONObject(i);
            Artist artist = new Artist(artistinfo.getString("name"), artistinfo.get("id").toString());
            artistSearchResult.add(artist);
        }
        return artistSearchResult;
    }
    
    public static Set<Album> getAlbumByStringSearch(String keyword) throws IOException, ElementNotFoundException{
        Set<Album> albumSearchResult = new HashSet<>();
        Connection.Response res = getSearchResult(keyword, SEARCH_TYPE_ALBUM);
        JSONObject json = (JSONObject) JSON.parse(res.body());
        JSONArray albumjson = json.getJSONObject("result").getJSONArray("albums");
        if(albumjson.size() == 0)
            throw new ElementNotFoundException();
        for(int i = 0; i < albumjson.size(); i++) {
            JSONObject albuminfo = albumjson.getJSONObject(0);
            JSONObject artistinfo = albuminfo.getJSONObject("artists");
            Artist artist = new Artist(artistinfo.getString("name"), artistinfo.get("id").toString());
            Album album =  new Album(artist, albuminfo.getString("name"), albuminfo.get("id").toString());
            albumSearchResult.add(album);
        }
        return albumSearchResult;
    }
    
    public static Set<Playlist> getPlaylistByStringSearch(String keyword) throws IOException, ElementNotFoundException{
        Set<Playlist> playlistSearchResult = new HashSet<>();
        Connection.Response res = getSearchResult(keyword, SEARCH_TYPE_PLAYLIST);
        JSONObject json = (JSONObject) JSON.parse(res.body());
        JSONArray playlistjson = json.getJSONObject("result").getJSONArray("playlists");
        if(playlistjson.size() == 0)
            throw new ElementNotFoundException();
        for(int i = 0; i < playlistjson.size(); i++) {
            JSONObject playlistinfo = playlistjson.getJSONObject(i);
            Playlist playlist = new Playlist(playlistinfo.get("id").toString(), playlistinfo.getString("name"));
            playlistSearchResult.add(playlist);
        }
        return playlistSearchResult;
    }
    
//    public static void main(String[] args) {
//        Set<Playlist> a = null; 
//        try {
//            a = getPlaylistByStringSearch("yoko kanno");
//        } catch (IOException | ElementNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        a.forEach(b -> System.out.println(b.getTitle() + " " + b.getId()));
//    }
//    
}

