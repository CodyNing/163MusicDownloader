package ca.bcit.cst.rongyi.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Downloader {

    public static void downloadSong(Song song, File dir) {
        downloadSong(song.title, song.downloadURL, dir);
    }

    public static void downloadSong(String name, String url, File dir) {
        try {
            URL website = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            File ofp = new File(dir, name+".mp3");
            FileOutputStream fos = new FileOutputStream(ofp);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            rbc.close();
            fos.close();
            System.out.printf("%s Downloaded\n", ofp.getName());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
