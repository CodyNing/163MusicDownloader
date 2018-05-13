package util;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ui.Center;

import java.util.Collection;
import java.util.LinkedList;

public class Searcher {

    private static ObservableList<Song> searchList = FXCollections.synchronizedObservableList(FXCollections.observableList(new LinkedList<Song>()));
    
    public static ObservableList<Song> getSearchList() {
        return searchList;
    }

    public static void setSearchList(Collection<Song> searchList) {
        Platform.runLater(() -> {
            Searcher.searchList.clear();
            Searcher.searchList.addAll(searchList);
            Center.printToStatus("Searching Success");
        });
    }

}
