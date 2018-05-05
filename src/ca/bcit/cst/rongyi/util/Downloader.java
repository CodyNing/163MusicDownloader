package ca.bcit.cst.rongyi.util;

import com.mpatric.mp3agic.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Downloader {

    public static File SONG_DIR = new File("./songs/");

    private static final Downloader downloader = new Downloader();

    public static Downloader getInstance() {
        return downloader;
    }

    private Downloader() {
        init();
    }

    private void init() {
        if (!SONG_DIR.exists())
            SONG_DIR.mkdir();
    }

    private final ConcurrentLinkedQueue<Download> downloadList = new ConcurrentLinkedQueue<>();
    private int maxConcurrentDownload = 5;
    private int currentDownloading = 0;

    public static String makeStringValidForWindowsFile(String str) {
        return str
                .replace(':', '：')
                .replace('<', '＜')
                .replace('>', '＞')
                .replace('\"', '＂')
                .replace('/', '／')
                .replace('\\', '｜')
                .replace('?', '？')
                .replace('*', '＊');
    }

    /**
     * Adds a download to the queue, start downloading if allow
     *
     * @param download the download to be added
     */
    private synchronized void addDownload(Download download) {
        downloadList.add(download);
        startHeadDownload();
    }

    private synchronized void startHeadDownload() {
        if (isAllowedToDownload()) {
            if (!downloadList.isEmpty()) {
                downloadList.poll().start();
                currentDownloading += 1;
            }
        }
    }

    private void setTag(Song song, File fp) throws InvalidDataException, IOException, UnsupportedTagException, NotSupportedException {
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
        System.out.printf("Set mp3 tags for song %s successfully\n", song.getTitle());
        new File(newFileName);
    }

    private boolean isAllowedToDownload() {
        return currentDownloading < maxConcurrentDownload;
    }

    /**
     * Download the given song to the given directory
     *
     * @param song the song to be downloaded
     * @param dir  the directory to save the file
     * @return the downloaded mp3 file
     */
    public void downloadSong(Song song, File dir) {
        System.out.printf("add song %s to download list\n", song.getTitle());
        song.setArtistAndAlbum();
        String targetFileName = dir.getAbsolutePath() + "\\" + song.getArtist().getName() + " - " + song.getTitle();
        File ofp = new File(targetFileName + ".mp3");
        // if a file with the same name already exist, do not download it
        // just return the existed file
        if (ofp.exists()) {
            System.out.printf("Song: %s already downloaded\n", song.getTitle());
            return;
        }
        File file = new File(targetFileName + "_temp.mp3");
        Download download = new Download(file, song);
        addDownload(download);

    }

    public void setMaxConcurrentDownload(int maxConcurrentDownload) {
        this.maxConcurrentDownload = maxConcurrentDownload;
    }

    private class Download extends Thread {

        private final Song song;
        private final File outputFile;

        Download(File outputFile, Song song) {
            this.song = song;
            this.outputFile = outputFile;
        }

        private void download() throws MalformedURLException {
            song.setDownloadURL();
            URL website = new URL(song.getDownloadURL());
            ReadableByteChannel rbc = null;
            FileOutputStream fos = null;
            try {
                rbc = Channels.newChannel(website.openStream());
                fos = new FileOutputStream(outputFile);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                System.out.printf("%s Download Complete\n", outputFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    rbc.close();
                    fos.close();
                } catch (NullPointerException | IOException e) {
                    // let it go
                }
            }

        }

        private void finishedDownload() {
            currentDownloading -= 1;
            startHeadDownload();
            try {
                setTag(song, outputFile);
            } catch (InvalidDataException | UnsupportedTagException | NotSupportedException | IOException e) {
                System.err.println("Fail to set mp3 tags for song " + song);
            }
        }

        @Override
        public void run() {
            try {
                System.out.printf("start downloading Song: %s\n", song.getTitle());
                download();
                finishedDownload();
            } catch (MalformedURLException e) {
                System.err.printf("URL: %s does not work\n", song.getDownloadURL());
            }
        }

    }

}
