package ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXProgressBar;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import util.Downloader;

class DownloadCell extends JFXListCell<Downloader.Download> {

    public DownloadCell() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem cancelItem = new MenuItem("Cancel");
        cancelItem.setOnAction(event -> this.getItem().cancelDownload());

        contextMenu.getItems().addAll(cancelItem);
        setContextMenu(contextMenu);
    }

    @Override
    protected void updateItem(Downloader.Download download, boolean empty) {
        super.updateItem(download, empty);
        if (download != null) {
            BorderPane node = new BorderPane();

            Label songLabel = new Label(download.getSong().getTitleProperty() + " - " + download.getStatus());
            node.setLeft(songLabel);

            JFXProgressBar progressBar = new JFXProgressBar();
            progressBar.progressProperty().bind(download.progressProperty());
            JFXButton cancelButton = new JFXButton("Cancel");
            cancelButton.setStyle("-fx-text-fill:WHITE;-fx-background-color:#d50000;-fx-font-size:14px;");
            cancelButton.setButtonType(JFXButton.ButtonType.RAISED);
            cancelButton.setOnAction(event -> download.cancelDownload());
            HBox right = new HBox(progressBar, cancelButton);
            right.setAlignment(Pos.CENTER);
            right.setSpacing(10.0);
            node.setRight(right);

            setGraphic(node);
            setText("");
        } else {
            setGraphic(null);
        }
    }


}
