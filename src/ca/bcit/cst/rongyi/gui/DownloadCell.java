package ca.bcit.cst.rongyi.gui;

import ca.bcit.cst.rongyi.util.Downloader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;

class DownloadCell extends ListCell<Downloader.Download> {

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
