package ca.bcit.cst.rongyi.util;

import com.mpatric.mp3agic.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Downloader {

    public static File SONG_DIR = new File("./songs/");

    private static final Downloader downloader = new Downloader();
    public static Downloader getInstance() { return downloader; }

    private Downloader() {
        init();
    }

    private void init() {
        if (!SONG_DIR.exists())
            SONG_DIR.mkdir();
    }

    public File setTag(Song song, File fp) throws InvalidDataException, IOException, UnsupportedTagException, NotSupportedException {
        Mp3File mp3file = new Mp3File(fp);
        ID3v2 id3v2Tag;
        if (mp3file.hasId3v2Tag()) {
            id3v2Tag = mp3file.getId3v2Tag();
        } else {
            // mp3 does not have an ID3v2 tag, let's create one..
            id3v2Tag = new ID3v24Tag();
            mp3file.setId3v2Tag(id3v2Tag);
        }
        id3v2Tag.setArtist(song.getArtist().getName());
        id3v2Tag.setTitle(song.getTitle());
        id3v2Tag.setAlbum(song.getAlbum().getName());
        String newFileName = fp.getParent() + "\\" + id3v2Tag.getArtist() + " - " + id3v2Tag.getTitle() + ".mp3";
        mp3file.save(newFileName);
        fp.delete();
        return new File(newFileName);
    }

    public File downloadSong(Song song, File dir) throws IOException {
        String targetFileName = dir.getAbsolutePath() + "\\" + song.getArtist().getName() + " - " + song.getTitle();
        File ofp = new File(targetFileName + ".mp3");
        // if a file with the same name already exist, do not download it
        if (ofp.exists()) {
            System.out.printf("Song: %s already downloaded\n", song.getTitle());
            return ofp;
        }
        File file = downloadSong(new File(targetFileName + "_temp.mp3"), song.getDownloadURL());
        try {
            setTag(song, file);
        } catch (InvalidDataException | UnsupportedTagException | NotSupportedException e) {
            e.printStackTrace();
        }
        return file;
    }

    public File downloadSong(File ofp, String url) throws IOException {
        URL website = new URL(url);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(ofp);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        rbc.close();
        fos.close();
        System.out.printf("%s Download Complete\n", ofp.getName());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ofp;
    }

    public File downloadSong(String name, String url, File dir) throws IOException {
        return downloadSong(new File(dir, name + ".mp3"), url);
    }

    public static byte[] MD5(File file) {
        try {
            FileInputStream stream = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = stream.read(buffer, 0 ,1024)) != -1) {
                md.update(buffer, 0, length);
            }

            stream.close();

            return md.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
