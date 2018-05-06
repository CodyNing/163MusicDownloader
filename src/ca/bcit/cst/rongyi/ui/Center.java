package ca.bcit.cst.rongyi.ui;

import ca.bcit.cst.rongyi.util.Downloader;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class Center {

    private static Label downloadStatus;

    private static Label statusLabel;

    public static void setLabel(Label downloadStatus, Label statusLabel) {
        Center.downloadStatus = downloadStatus;
        Center.statusLabel = statusLabel;
    }

    public static void updateListStatus() {
        Platform.runLater(() -> {
            Downloader downloader = Downloader.getInstance();
            int downloading = downloader.getCurrentDownloading();
            int size = downloader.getDownloadList().size();

            String status = String.format(" %s / %s downloading", downloading, size);
            downloadStatus.setText(status);
        });
    }

    public static void printToStatus(String status) {
        Platform.runLater(() -> statusLabel.setText(status));
    }

}
