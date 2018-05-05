package ca.bcit.cst.rongyi.gui;

import ca.bcit.cst.rongyi.util.Downloader;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;

public class StatusBar extends HBox {

    private final Label listStatus = new Label();
    private final Label statusLabel = new Label();

    public StatusBar() {
        this.setSpacing(10.0);
        this.getChildren().addAll(listStatus, getSeparator(), statusLabel);
        updateListStatus();
    }

    public void updateListStatus() {
        Downloader downloader = Downloader.getInstance();
        int downloading = downloader.getCurrentDownloading();
        int size = downloader.getDownloadList().size();

        String status = String.format("%s / %s downloading", downloading, size);
        setListStatus(status);
    }

    private void setListStatus(String status) {
        listStatus.setText(status);
    }

    private Separator getSeparator() {
        return new Separator(Orientation.VERTICAL);
    }

    public void setStatusLabel(String status) {
        statusLabel.setText(status);
    }
}
