package util;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ui.Center;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Searcher {

    private static ObservableList<Song> searchList = FXCollections.synchronizedObservableList(FXCollections.observableList(new LinkedList<Song>()));
    
    public static final ExecutorService searchThread = Executors.newSingleThreadExecutor();

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

    public static ExecutorService getSearchThread() {
        return searchThread;
    }

    public static void setSearchList(ObservableList<Song> searchList) {
        Searcher.searchList = searchList;
    }


}
