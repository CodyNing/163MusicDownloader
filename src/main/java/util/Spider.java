package util;

import entity.Album;
import entity.Artist;
import entity.Playlist;
import entity.Song;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import ui.Center;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Spider {

    private static final String PLAYLIST_URL = "http://music.163.com/api/playlist/detail?id=";
    private static final String SONG_URL = "http://music.163.com/weapi/v3/song/detail";
    private static final String ALBUM_URL = "http://music.163.com/weapi/v1/album/";
    private static final String ARTIST_URL = "http://music.163.com/weapi/artist/albums/";
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
    
    private static Response getApiResponse(String url, Connection.Method contype, JSONObject data) throws IOException {
        Response response = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:57.0) Gecko/20100101 Firefox/57.0")
            .header("Accept", "*/*")
            .header("Referer", "http://music.163.com")
            .header("Connection", "keep-alive")
            .header("Host", "music.163.com")
            .header("Accept-Language", "zh-CN,en-US;q=0.7,en;q=0.3")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .data(EncryptUtils.encrypt(data.toJSONString()))
            .method(contype)
            .ignoreContentType(true)
            .timeout(10000)
            .execute();
        return response;
    }
    
    public static String getDLURLfromHeader(String url, String id) throws IOException {
        System.out.println(url + " " + id);
//        String regex = "\\&c=(.*)";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(url);
//        String c = null;
//        if (matcher.find())
//            c = matcher.group(1);
//        else
//            throw new IOException();
//        c = URLDecoder.decode(c, "UTF-8");
        Connection.Response
                response = Jsoup.connect("http:" + url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36")
                .header("X-DevTools-Emulate-Network-Conditions-Client-Id", "50ED8223EED918DD6579B892A1DE1E2A")
                .method(Connection.Method.GET)
//                .data("id", id)
//                .data("c", c)
                .followRedirects(false)
                .timeout(10000)
                .execute();
        String dlurl = response.header("location");
        System.out.println(dlurl);
        return dlurl;
    }
    
    public static Response getSearchResult(String keyword, String type) throws IOException {
        JSONObject json = new JSONObject();
        json.put("s", keyword);
        json.put("type",type);
        json.put("offset", 0);
        json.put("total", "True");
        json.put("limit", 50);
        
        return getApiResponse(SEARCH_URL, Connection.Method.POST, json);
    }
    
    public static String getSongDownloadURL(String songID) throws IOException {
        Element body = get163Connection(DOWNLOADER_URL)
                .data("id", songID)
                .get().body();
        String url = getDLURLfromHeader(body.select("a[class=button]").get(1).attr("href"), songID);
        return url;
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
        JSONObject data = new JSONObject();
        data.put("id", playlistId);
        data.put("n", "100000");
        data.put("csfn_token", "");
        JSONObject json = JSON.parseObject(getApiResponse(PLAYLIST_URL + playlistId, Method.POST, data).body());
        if((int)json.get("code") != 200)
            throw new ElementNotFoundException("cannot find playlist title, id: " + playlistId);
        JSONArray songlist = json.getJSONObject("result").getJSONArray("tracks");
        for(int i = 0; i < songlist.size(); i++) {
            JSONObject song = songlist.getJSONObject(i);
            songIDList.add(new Song(song.get("id").toString(), song.getString("name")));
        }

        return songIDList;
    }

    public static Playlist getPlaylistByID(String playlistId) throws IOException, ElementNotFoundException {
        Playlist playlist;
        if ((playlist = Database.getPlaylist(playlistId)) != null)
            return playlist;

        Set<Song> songIDList = new HashSet<>();
        JSONObject data = new JSONObject();
        data.put("id", playlistId);
        data.put("n", "100000");
        data.put("csfn_token", "");
        JSONObject json = JSON.parseObject(getApiResponse(PLAYLIST_URL + playlistId, Method.POST, data).body());
        if((int)json.get("code") != 200)
            throw new ElementNotFoundException("cannot find playlist title, id: " + playlistId);
        JSONArray songlist = json.getJSONObject("result").getJSONArray("tracks");
        for(int i = 0; i < songlist.size(); i++) {
            JSONObject song = songlist.getJSONObject(i);
            songIDList.add(new Song(song.get("id").toString(), song.getString("name")));
        }

        return new Playlist(playlistId, json.getString("name"), songIDList);
    }

    public static Album getAlbumByID(String albumID) throws IOException, ElementNotFoundException {
        Album album;
//        if ((album = Database.getAlbum(albumID)) != null)
//            return album;

        JSONObject data = new JSONObject();
        data.put("csfn_token", "");
        JSONObject json = JSON.parseObject(getApiResponse(ALBUM_URL + albumID, Method.POST, data).body());
        if((int)json.get("code") != 200)
            throw new ElementNotFoundException("cannot find album title, id: " + albumID);
        JSONObject jsonALbum = json.getJSONObject("album");
        JSONObject jsonArtist = jsonALbum.getJSONObject("artist");
        JSONArray jsonSong = json.getJSONArray("songs");
        Artist artist = new Artist(jsonArtist.getString("name"), jsonArtist.get("id").toString());
        album = new Album(artist, jsonALbum.getString("name"), albumID);
        for(int i = 0; i < jsonSong.size(); i++) {
            JSONObject song = jsonSong.getJSONObject(i);
            album.addSong(new Song(song.get("id").toString(), song.getString("name"), song.get("no").toString(), artist, album));
        }

        return album;
    }

    public static Song getSongByID(String songId) throws IOException, ElementNotFoundException {
        Song song;
        if ((song = Database.getSong(songId)) != null)
            return song;
        JSONObject data = new JSONObject();
        data.put("ids", "[" + songId + "]");
        data.put("c", "[{id :" + songId + "}]");
        data.put("csfn_token", "");
        JSONObject json = JSON.parseObject(getApiResponse(SONG_URL, Method.POST, data).body());
        JSONObject songjson = json.getJSONArray("songs").getJSONObject(0);
        JSONObject artistjson = songjson.getJSONArray("ar").getJSONObject(0);
        JSONObject albumjson = songjson.getJSONObject("al");
        if((int)json.get("code") != 200)
            throw new ElementNotFoundException("Unable to get song, id: " + songId);
        Artist artist = new Artist(artistjson.getString("name"), artistjson.get("id").toString());
        Album album = new Album(artist, albumjson.getString("name"), albumjson.get("id").toString());
        
        return new Song(songId, songjson.getString("name"), artist, album);
    }

    public static Artist getArtistByID(String artistID) throws IOException, ElementNotFoundException {
        Artist artist;
//        if ((artist = Database.getArtist(artistID)) != null)
//            return artist;
        
        JSONObject data = new JSONObject();
        data.put("offset", 0);
        data.put("total", "True");
        data.put("limit", DISPLAY_LIMIT);
        data.put("csfn_token", "");
        JSONObject json = JSON.parseObject(getApiResponse(ARTIST_URL + artistID, Method.POST, data).body());
        if((int)json.get("code") != 200)
            throw new ElementNotFoundException("Unable to get artist, id: " + artistID);
        Set<Album> albumSet = new HashSet<>();
        JSONArray albumjson = json.getJSONArray("hotAlbums");
        for(int i = 0; i < albumjson.size(); i++) {
            albumSet.add(getAlbumByID(albumjson.getJSONObject(i).get("id").toString()));
        }
        artist = new Artist(json.getJSONObject("artist").getString("name"), artistID, albumSet);

        return artist;
    }

    public static void setArtistAndAlbum(Song song) throws ElementNotFoundException, IOException {
        JSONObject data = new JSONObject();
        data.put("ids", "[" + song.getId() + "]");
        data.put("c", "[{id :" + song.getId() + "}]");
        data.put("csfn_token", "");
        JSONObject json = JSON.parseObject(getApiResponse(SONG_URL, Method.POST, data).body());
        JSONObject songjson = json.getJSONArray("songs").getJSONObject(0);
        JSONObject artistjson = songjson.getJSONArray("ar").getJSONObject(0);
        JSONObject albumjson = songjson.getJSONObject("al");
        if((int)json.get("code") != 200)
            throw new ElementNotFoundException("Unable to get song, id: " + song.getId());
        Artist artist = new Artist(artistjson.getString("name"), artistjson.get("id").toString());
        Album album = new Album(artist, albumjson.getString("name"), albumjson.get("id").toString());
        song.setAlbum(album);
        song.setArtist(artist);
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
//        JSONObject data = new JSONObject();
////        data.put("offset", "0");
////        data.put("total", "true");
////        data.put("limit", "30");
//        data.put("ids", "[528116240]");
//        data.put("c", "[{id : 528116240}]");
//        data.put("csfn_token", "");
////        data.put("br", "999000");
//        try {
//            System.out.println(getApiResponse(SONG_URL, Method.POST, data).body());
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
    
}

