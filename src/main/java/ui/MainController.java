package ui;

import com.jfoenix.controls.*;
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

// TODO Cody's added UI are too ugly, needs improvement (try to implement Google Material Design
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
    private JFXProgressBar searchProgress;
    
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
        searchView.setItems(Searcher.getSearchList());
        searchView.setCellFactory(cell -> new SearchCell());
        
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

    private void setUpRdToggle() {
        setUpRadioButton(new RunnableEvent.PlaylistSearchEvent(), "playlist");
        setUpRadioButton(new RunnableEvent.ArtistSearchEvent(), "artist");
        setUpRadioButton(new RunnableEvent.AlbumSearchEvent(), "album");
        setUpRadioButton(new RunnableEvent.SongSearchEvent(), "song");

        selectToggle.selectedToggleProperty().addListener(
                event -> Center.setUpIdValidationTextField(
                        ((ToggleData) selectToggle.getSelectedToggle().getUserData()).getData(), searchBox));

        selectToggle.selectToggle(selectToggle.getToggles().get(0));
    }

    private void setUpRadioButton(RunnableEvent event, String data) {
        JFXRadioButton radioButton = new JFXRadioButton(data.substring(0, 1).toUpperCase() + data.substring(1));
        radioButton.setPadding(new Insets(10));
        radioButton.setToggleGroup(selectToggle);
        radioButton.setUserData(new ToggleData(event, data));
        selectType.getChildren().add(radioButton);
    }
    
    public void search() {
        if (selectToggle.getSelectedToggle() != null && searchBox.validate()) {
            // Start a new Thread to search in background
            String id = searchBox.getText();
            Center.printToStatus("Searching in process...");
            RunnableEvent event = ((ToggleData) selectToggle.getSelectedToggle().getUserData()).getEvent();
            ReadIDTask searchTask = new ReadIDTask(id, event);
            searchProgress.visibleProperty().bind(searchTask.runningProperty());
            new Thread(searchTask).start();
        }
    }
    
}
