package ca.bcit.cst.rongyi.util;

import com.mpatric.mp3agic.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Downloader {

    public static final String DEFAULT_DIR = "./songs/";
    private static Set<File> localFiles = new HashSet<>();

    public static void updateLocalFileSet() {
        File dir = new File(DEFAULT_DIR);
        FilenameFilter filenameFilter = (file, s) -> s.substring(s.lastIndexOf(".")).equals(".mp3");
        File[] files = dir.listFiles(filenameFilter);
        Collections.addAll(localFiles, files);
    }

    public static void setTag(Song song, File fp) throws InvalidDataException, IOException, UnsupportedTagException, NotSupportedException {
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
    }

    public static File downloadSong(Song song, File dir) throws IOException {
        return downloadSong(song.getTitle(), song.getDownloadURL(), dir);
    }

    public static File downloadSong(String name, String url, File dir) throws IOException {
        File ofp;

        URL website = new URL(url);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        ofp = new File(dir, name + ".mp3");
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

    public static void main(String[] args) {
        Downloader.updateLocalFileSet();
    }

}
