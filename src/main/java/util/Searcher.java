package util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ui.Center;

public class Searcher {

    private static ObservableList<Song> searchlist = FXCollections.synchronizedObservableList(FXCollections.observableList(new LinkedList<Song>()));
    
    public static final ExecutorService searchThread = Executors.newSingleThreadExecutor();
    
    public static ObservableList<Song> getSearchlist() {
        return searchlist;
    }

    public static ExecutorService getSearchthread() {
        return searchThread;
    }

    public static void setSearchlist(ObservableList<Song> searchlist) {
        Searcher.searchlist = searchlist;
    }
    
    public static void setSearchlist(Collection<Song> searchlist) {
        Platform.runLater(() -> {
            Searcher.searchlist.clear();
            Searcher.searchlist.addAll(searchlist);
            Center.printToStatus("Searching Success");
        });
    }
    
    
}
