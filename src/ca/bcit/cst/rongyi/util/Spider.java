package ca.bcit.cst.rongyi.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Spider {

    public static final String BASE_URL = "http://music.163.com";
    public static final String PLAYLIST_URL = "http://music.163.com/playlist";
    public static final String SONG_URL = "http://music.163.com/song";
    public static final String DOWNLOADER_URL = "https://ouo.us/fm/163/";

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

    public static String getSongDownloadURL(String songID) {
        String url = null;
        try {
            Element body = Jsoup.connect("https://ouo.us/fm/163/")
                    .data("id", songID)
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
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
                    .get().body();

            url = body.select("a[class=button]").get(1).attr("href");
        } catch (IOException e) {
            System.out.printf("Cannot get download URL, id: %s\n", songID);
        }

        return url;
    }

    public static List<Song> getSongs(List<String> songIDList) throws IOException, ElementNotFoundException {
        List<Song> songList = new ArrayList<>();
        for (String songID : songIDList) {
            songList.add(getSongByID(songID));
        }

        return songList;
    }

    public static List<Song> getCompleteSongByPlaylist(String playlistID) throws IOException, ElementNotFoundException {
        List<Song> songList = getSongByPlaylist(playlistID);
        songList.forEach(song -> song.setArtistAndAlbum());
        return songList;
    }

    public static List<Song> getSongByPlaylist(String playlistId) throws IOException, ElementNotFoundException {
        List<Song> songIDList = new ArrayList<>();

        Element body = get163Connection(PLAYLIST_URL)
                .data("id", playlistId)
                .get().body();

        Elements eleSongList = body.selectFirst("ul[class=f-hide]").select("a[href]");
        if (eleSongList.size() == 0)
            throw new ElementNotFoundException("Unable to get playlist, id: " + playlistId);
        eleSongList.forEach(song -> {
                    songIDList.add(new Song(song.attr("href").substring(9), song.text()));
                }
        );

        return songIDList;
    }

    public static Song getSongByID(String songId) throws IOException, ElementNotFoundException {
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
        Album album = new Album(eleAlbum.text(), eleAlbum.attr("href").substring(10));

        return new Song(songId, songTitle, artist, album);
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
            Element eleArtist = eleInfo.get(0);
            Artist artist = new Artist(eleArtist.text(), eleArtist.attr("href").substring(11));
            Element eleAlbum = eleInfo.get(1);
            Album album = new Album(eleAlbum.text(), eleAlbum.attr("href").substring(10));
            song.setArtist(artist);
            song.setAlbum(album);
        } catch (IOException e) {
            System.out.printf("Cannot Artist and Album from song, id: \n", song.getId());
        }
    }

}

