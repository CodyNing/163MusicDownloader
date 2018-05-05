package ca.bcit.cst.rongyi.gui;

import javafx.application.Platform;

public class Center {
    private static StatusBar statusBar;

    public static void setStatusBar(StatusBar statusBar) {
        if (Center.statusBar != null)
            return;
        Center.statusBar = statusBar;
    }

    public static void updateListStatus() {
        Platform.runLater(() -> statusBar.updateListStatus());
    }

    public static void printToStatus(String status) {
        Platform.runLater(() -> statusBar.setStatusLabel(status));
    }

}
