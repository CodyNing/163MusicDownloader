package ui;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import util.Downloader;
import util.Searcher;
import util.Song;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {

    @FXML
    private Label downloadStatus;

    @FXML
    private Label statusLabel;
    
    @FXML
    private JFXListView<Downloader.Download> listView;
    
    @FXML
    private JFXListView<Song> searchView;
    
    @FXML
    private HBox selectType;
    
    @FXML
    private JFXTextField searchBox;
    
    @FXML
    private JFXHamburger titleBurger;

    @FXML
    private JFXRippler titleRippler;

    @FXML
    private JFXHamburger optionBurger;

    @FXML
    private JFXRippler optionRippler;

    private JFXPopup downloadPopup;

    private JFXPopup optionPopup;
    
    private final ToggleGroup selectToggle = new ToggleGroup();

    public MainController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        loader.setRoot(this);
        loader.setController(this);
    }

    @PostConstruct
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Center.setLabel(downloadStatus, statusLabel);
        Center.updateListStatus();
        Center.printToStatus("Welcome to 163Music");
        
        listView.setItems(Downloader.getInstance().getDownloadList());
        listView.setCellFactory(cell -> new DownloadCell());
        searchView.setItems(Searcher.getSearchlist());
        searchView.setCellFactory(ceeeeelll -> new SearchCell());
        
        setUpRdToggle();
        
        try {
            downloadPopup = new JFXPopup(FXMLLoader.load(getClass().getResource("/fxml/ui/DownloadPopup.fxml")));
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
        }
        titleBurger.setOnMouseClicked(event -> downloadPopup.show(titleRippler, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, 50.0, 10.0));

        try {
            optionPopup = new JFXPopup(FXMLLoader.load(getClass().getResource("/fxml/ui/OptionPopup.fxml")));
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
        }
        optionBurger.setOnMouseClicked(event -> optionPopup.show(optionRippler, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT, -50.0, 10.0));

    }

    interface ToggleData{
        DownloadEvent getEvent();
    }
    
    private void setUpRdToggle() {
        JFXRadioButton playlistRd = new JFXRadioButton("Playlist");
        playlistRd.setPadding(new Insets(10));
        playlistRd.setToggleGroup(selectToggle);
        playlistRd.setUserData(new ToggleData() {
            
            @Override
            public DownloadEvent getEvent(){
                return new DownloadEvent.PlaylistSearchEvent();
            }
            
            @Override
            public String toString() {
                return "playlist";
            }
        });
        JFXRadioButton artistRd = new JFXRadioButton("Artist");
        artistRd.setPadding(new Insets(10));
        artistRd.setToggleGroup(selectToggle);
        artistRd.setUserData(new ToggleData() {
            
            @Override
            public DownloadEvent getEvent(){
                return new DownloadEvent.ArtistSearchEvent();
            }
            
            @Override
            public String toString() {
                return "artist";
            }
        });
        JFXRadioButton albumRd = new JFXRadioButton("Album");
        albumRd.setPadding(new Insets(10));
        albumRd.setToggleGroup(selectToggle);
        albumRd.setUserData(new ToggleData() {
            
            @Override
            public DownloadEvent getEvent(){
                return new DownloadEvent.AlbumSearchEvent();
            }
            
            @Override
            public String toString() {
                return "album";
            }
        });
        JFXRadioButton songRd = new JFXRadioButton("Song");
        songRd.setPadding(new Insets(10));
        songRd.setToggleGroup(selectToggle);
        songRd.setUserData(new ToggleData() {
            
            @Override
            public DownloadEvent getEvent(){
                return new DownloadEvent.SongSearchEvent();
            }
            
            @Override
            public String toString() {
                return "song";
            }
        });
        selectType.getChildren().addAll(playlistRd, artistRd, albumRd, songRd);
        selectToggle.selectedToggleProperty().addListener(
                event -> DownloadPopupController.setUpidValidatedTextField(
                        selectToggle.getSelectedToggle().getUserData().toString(), searchBox));
    }
    
    public void search() {
        if (selectToggle.selectedToggleProperty() != null && searchBox.validate()) {
            // Start a new Thread to search in background
            String id = searchBox.getText();

            new Thread(new ReadIDTask(id, ((ToggleData) selectToggle.getSelectedToggle().getUserData()).getEvent())).start();
        }
    }
    
}
