package ui;

import com.jfoenix.controls.*;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import util.Downloader;
import util.Song;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

public class MainController implements Initializable {

    @FXML
    private Label downloadStatus;

    @FXML
    private Label statusLabel;

    @FXML
    private JFXListView<Downloader.Download> listView;

    @FXML
    private JFXTreeTableView<Song> searchView;

    @FXML
    private JFXTreeTableColumn<Song, String> titleColumn;

    @FXML
    private JFXTreeTableColumn<Song, String> artistColumn;

    @FXML
    private JFXTreeTableColumn<Song, String> albumColumn;

    @FXML
    private JFXTreeTableColumn<Song, String> actionColumn;

    @FXML
    private JFXProgressBar searchProgress;

    @FXML
    private HBox selectType;

    @FXML
    private JFXTextField searchTextField;

    @FXML
    private JFXHamburger titleBurger;

    @FXML
    private JFXRippler titleRippler;

    @FXML
    private JFXHamburger optionBurger;

    @FXML
    private JFXRippler optionRippler;

    @FXML
    private JFXTextField searchFilterField;

    @FXML
    private Label searchListLabel;

    @FXML
    private Label selectionLabel;

    @FXML
    private BorderPane searchListBar;

    private JFXPopup downloadPopup;

    private JFXPopup optionPopup;

    private ToggleGroup selectToggle = new ToggleGroup();


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

        setUpRdToggle();

        initSearchView();

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
                        ((ToggleData) selectToggle.getSelectedToggle().getUserData()).getData(), searchTextField));

        selectToggle.selectToggle(selectToggle.getToggles().get(0));
    }

    private void setUpRadioButton(RunnableEvent event, String data) {
        JFXRadioButton radioButton = new JFXRadioButton(data.substring(0, 1).toUpperCase() + data.substring(1));
        radioButton.setPadding(new Insets(10));
        radioButton.setToggleGroup(selectToggle);
        radioButton.setUserData(new ToggleData(event, data));
        selectType.getChildren().add(radioButton);
    }

    @FXML
    public void search() {
        if (selectToggle.getSelectedToggle() != null && searchTextField.validate()) {
            // Start a new Thread to search in background
            String id = searchTextField.getText();
            searchTextField.clear();
            Center.printToStatus("Searching in process...");
            RunnableEvent event = ((ToggleData) selectToggle.getSelectedToggle().getUserData()).getEvent();
            ReadIDTask searchTask = new ReadIDTask(id, event);
            searchProgress.visibleProperty().bind(searchTask.runningProperty());
            Thread thread = new Thread(searchTask);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void initSearchView() {
        searchView.setEditable(false);
        searchView.setShowRoot(false);
        searchView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        setupCellValueFactory(titleColumn, Song::titlePropertyProperty);
        setupCellValueFactory(artistColumn, Song::artistNameProperty);
        setupCellValueFactory(albumColumn, Song::albumNameProperty);
        setupCellValueFactory(actionColumn, Song::IDPropertyProperty);
        actionColumn.setCellFactory(param -> new TreeTableCell<Song, String>() {
            @Override
            protected void updateItem(String id, boolean empty) {
                if (!empty) {
                    JFXButton button = new JFXButton("Download");
                    button.setStyle("-fx-text-fill:WHITE;-fx-background-color:#5264AE;-fx-font-size:14px;");
                    button.setButtonType(JFXButton.ButtonType.RAISED);
                    button.setOnAction(event -> new Thread(new ReadIDTask(id, new RunnableEvent.SongDownloadEvent())).start());
                    setGraphic(button);
                    setText("");
                } else {
                    setGraphic(null);
                }
            }
        });

        searchFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchView.setPredicate(songProp -> {
                final Song song = songProp.getValue();
                String filter = newValue.toLowerCase();
                return song.getTitle().toLowerCase().contains(filter)
                        || (song.getArtist() != null && song.getArtist().getName().toLowerCase().contains(filter))
                        || (song.getAlbum() != null && song.getAlbum().getName().toLowerCase().contains(filter));
            });
        });
        selectionLabel.textProperty().bind(Bindings.createStringBinding(() -> searchView.getSelectionModel().getSelectedCells().size() + " song(s) selected",
                searchView.getSelectionModel().getSelectedItems()));

        searchListBar.setPadding(new Insets(0.0, 10.0, 0.0, 10.0));

        Center.setSearchView(searchView);
        Center.setSearchListLabel(searchListLabel);
    }

    private <T> void setupCellValueFactory(JFXTreeTableColumn<Song, T> column, Function<Song, ObservableValue<T>> mapper) {
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<Song, T> param) -> {
            if (column.validateValue(param)) {
                return mapper.apply(param.getValue().getValue());
            } else {
                return column.getComputedValue(param);
            }
        });
    }

    @FXML
    public void downloadAll() {
        for (TreeItem<Song> songTreeItem : searchView.getRoot().getChildren()) {
            songTreeItem.getValue().download();
        }
    }

    @FXML
    public void downloadSelected() {
        for (TreeTablePosition<Song, ?> cell : searchView.getSelectionModel().getSelectedCells()) {
            cell.getTreeItem().getValue().download();
        }
    }

}
