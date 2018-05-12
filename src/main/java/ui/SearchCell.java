package ui;

import util.Song;

import com.jfoenix.controls.JFXListCell;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

class SearchCell extends JFXListCell<Song> {

    public SearchCell() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem downloadItem = new MenuItem("Download");
        downloadItem.setOnAction(event -> this.getItem().download());

        contextMenu.getItems().addAll(downloadItem);
        setContextMenu(contextMenu);
    }

    @Override
    protected void updateItem(Song item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            setText(item.toString());
        } else {
            setText("");
        }
    }


}
