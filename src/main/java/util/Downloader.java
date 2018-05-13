package util;

import com.mpatric.mp3agic.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import ui.Center;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.LinkedList;

public class Downloader {

    public static final File TEMP_DIR = new File("temp/");

    private static final Downloader downloader = new Downloader();

    public static Downloader getInstance() {
        return downloader;
    }

    private final ObservableList<Download> downloadList;

    private Downloader() {
        if (!Database.getSongDir().exists())
            Database.getSongDir().mkdir();
        if (!TEMP_DIR.exists())
            TEMP_DIR.mkdir();
        downloadList = FXCollections.synchronizedObservableList(FXCollections.observableList(new LinkedList<Download>()));
        downloadList.addListener((ListChangeListener<Download>) c -> Center.updateListStatus());
    }

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
        Platform.runLater(() -> {
            downloadList.add(download);
            startHeadDownload();
        });
    }

    private synchronized void startHeadDownload() {
        if (isAllowedToDownload() && downloadList.size() > currentDownloading) {
            Thread thread = new Thread(downloadList.get(currentDownloading));
            thread.setDaemon(true);
            thread.start();
            currentDownloading += 1;
            Center.updateListStatus();
        }
    }

    /**
     * Download the given song to the given directory
     *
     * @param song the song to be downloaded
     * @param dir  the directory to save the file
     */
    public void downloadSong(Song song, File dir) {
        song.setArtistAndAlbum();
        if (song.exists()) {
            Center.printToStatus("Song: " + song.getTitle() + ", already downloaded");
            return;
        }
        File file = new File(dir, song.getArtist().getName() + " - " + song.getTitle() + "_temp.mp3");
        Download download = new Download(file, song);
        addDownload(download);
    }

    public void downloadSong(Collection<Song> songCollection) {
        for (Song song : songCollection) {
            song.download();
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
        id3v2Tag.setTrack(song.getTrackNo());
        String newFileName = Database.getSongDir() + "\\" + id3v2Tag.getArtist() + " - " + id3v2Tag.getTitle() + ".mp3";
        mp3file.save(newFileName);
        fp.delete();
        Center.printToStatus(id3v2Tag.getArtist() + " - " + id3v2Tag.getTitle() + " download Complete");
    }

    private boolean isAllowedToDownload() {
        return currentDownloading < Database.getInstance().getMaxConcurrentDownload();
    }

    public ObservableList<Download> getDownloadList() {
        return downloadList;
    }

    public int getCurrentDownloading() {
        return currentDownloading;
    }

    public boolean hasTask() {
        return downloadList.size() != 0;
    }

    public class Download extends Task<Void> {

        private final Song song;
        private final File outputFile;

        Download(File outputFile, Song song) {
            this.song = song;
            this.outputFile = outputFile;
            this.setOnSucceeded(event -> {
                currentDownloading -= 1;
                downloadList.remove(this);
                startHeadDownload();
                try {
                    setTag(song, outputFile);
                } catch (InvalidDataException | UnsupportedTagException | NotSupportedException e) {
                    System.err.println(e.getClass() + " Fail to set mp3 tags for song " + song);
                } catch (IOException e) {
                    // let it go.. usually because the song cannot be downloaded
                }
            });
        }

        private void download() throws MalformedURLException {
            song.setDownloadURL();
            if (song.getDownloadURL() == null) {
                Center.printToStatus("Unable to get URL for song " + song.getTitle() + ", append task at the end of download list.");
                addDownload(new Download(outputFile, song));
                return;
            }
            URL website = new URL(song.getDownloadURL());
            ReadableByteChannel rbc = null;
            FileOutputStream fos = null;
            try {
                rbc = Channels.newChannel(website.openStream());
                fos = new FileOutputStream(outputFile);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            } catch (IOException e) {
                System.err.println("Unable to download from " + song.getDownloadURL());
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

        @Override
        public String toString() {
            return song.getArtist().getName() + " - " + song.getTitle() + " - " + getStatus();
        }

        public String getStatus() {
            return (this.isRunning() ? "Downloading" : "Pending");
        }

        @Override
        protected Void call() {
            try {
                download();
            } catch (MalformedURLException e) {
                System.err.printf("URL: %s does not work\n", song.getDownloadURL());
            }
            return null;
        }

        public void cancelDownload() {
            downloadList.remove(this);
            if (this.getState() == State.RUNNING) {
                currentDownloading -= 1;
                startHeadDownload();
                if (outputFile.exists())
                    outputFile.delete();
            }
            this.cancel();
            Center.printToStatus("Cancelled download song: " + song.getTitle());
        }

        public Song getSong() {
            return song;
        }
    }

}
