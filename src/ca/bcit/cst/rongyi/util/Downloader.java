package ca.bcit.cst.rongyi.util;

import com.mpatric.mp3agic.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Downloader {

    private static final int MAX_CONCURRENT_DOWNLOAD = 5;
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

    private List<Download> downloadList = new LinkedList<>();

    /**
     *
     * @param download
     */
    public synchronized void addDownload(Download download) {
        downloadList.add(download);
        if (downloadList.size() < MAX_CONCURRENT_DOWNLOAD)
            download.start();
    }

    /**
     * @param song
     * @param fp
     * @return
     * @throws InvalidDataException
     * @throws IOException
     * @throws UnsupportedTagException
     * @throws NotSupportedException
     */
    public File setTag(Song song, File fp) throws InvalidDataException, IOException, UnsupportedTagException, NotSupportedException {
        song.setArtistAndAlbum();
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
        return new File(newFileName);
    }

    /**
     * Download the given song to the given directory
     *
     * @param song the song to be downloaded
     * @param dir  the directory to save the file
     * @return the downloaded mp3 file
     * @throws IOException
     */
    public File downloadSong(Song song, File dir) throws IOException {
        System.out.printf("start downloading Song: %s\n", song.getTitle());
        song.setDownloadURL();
        String targetFileName = dir.getAbsolutePath() + "\\" + song.getArtist().getName() + " - " + song.getTitle();
        File ofp = new File(targetFileName + ".mp3");
        // if a file with the same name already exist, do not download it
        // just return the existed file
        if (ofp.exists()) {
            System.out.printf("Song: %s already downloaded\n", song.getTitle());
            return ofp;
        }
        File file = new File(targetFileName + "_temp.mp3");
        Download download = new Download(file, song);
        download.addDownloadFinishedEvent(new PrioritizedEvent(()->{
            downloadList.remove(this);
            for (int i = 0 ; i < downloadList.size() && i < MAX_CONCURRENT_DOWNLOAD; i++) {
                startDownload(downloadList.get(i));
            }
        }, 10));
        download.start();

        return file;
    }

    /**
     * Start the given download if not yet started.
     * Only one download can be started at once.
     *
     * @param download the download to be started
     * @return true if started successfully, false if already started
     */
    private synchronized boolean startDownload(Download download) {
        if (!download.isAlive()) {
            download.start();
            return true;
        }
        return false;
    }

    public static byte[] MD5(File file) {
        try {
            FileInputStream stream = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = stream.read(buffer, 0, 1024)) != -1) {
                md.update(buffer, 0, length);
            }

            stream.close();

            return md.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private class Download extends Thread {

        private final Song song;
        private final File outputFile;
        private List<PrioritizedEvent> finishedEventList = new ArrayList<>();

        public Download(File outputFile, Song song) {
            this.song = song;
            this.outputFile = outputFile;
            // Set Mp3 Tags after download is completed
            addDownloadFinishedEvent(new PrioritizedEvent(() -> {
                try {
                    setTag(song, outputFile);
                } catch (InvalidDataException | UnsupportedTagException | NotSupportedException | IOException e) {
                    System.out.println("Fail to set mp3 tags for song " + song);
                }
            }));
        }

        private void download() throws MalformedURLException {
            URL website = new URL(song.getDownloadURL());
            ReadableByteChannel rbc = null;
            FileOutputStream fos = null;
            try {
                rbc = Channels.newChannel(website.openStream());
                fos = new FileOutputStream(outputFile);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                System.out.printf("%s Download Complete\n", outputFile.getName());
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    rbc.close();
                    fos.close();
                } catch(NullPointerException | IOException e) {
                    // let it go
                }
            }

        }

        @Override
        public void run() {
            try {
                download();
                finishedEventList.sort(null);
                for (PrioritizedEvent event : finishedEventList)
                    event.handle();
            } catch (MalformedURLException e) {
                System.out.printf("URL: %s does not work\n", song.getDownloadURL());
            }
        }

        public void addDownloadFinishedEvent(PrioritizedEvent downloadFinishedEvent) {
            this.finishedEventList.add(downloadFinishedEvent);
        }
    }

}
