package ui;

import util.Downloader;
import com.jfoenix.controls.JFXListCell;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

class DownloadCell extends JFXListCell<Downloader.Download> {

    public DownloadCell() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem cancelItem = new MenuItem("Cancel");
        cancelItem.setOnAction(event -> this.getItem().cancelDownload());

        contextMenu.getItems().addAll(cancelItem);
        setContextMenu(contextMenu);
    }

    @Override
    protected void updateItem(Downloader.Download item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            setText(item.toString());
        } else {
            setText("");
        }
    }


}
