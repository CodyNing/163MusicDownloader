package ui;

import com.jfoenix.controls.*;
import entity.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import util.Downloader;
import util.ThreadUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;

public class MainController implements Initializable {

    @FXML
    private Label statusLabel;

    @FXML
    private JFXListView<Downloader.Download> listView;

    @FXML
    private JFXTreeTableView<Entity> searchView;

    @FXML
    private JFXProgressBar searchProgress;

    @FXML
    private HBox selectType;
    
    @FXML
    private HBox switchType;

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
    private Tab downloadTab;

    @FXML
    private JFXButton downloadSelectedButton;

    @FXML
    private JFXButton downloadAllButton;

    private JFXPopup downloadPopup;

    private JFXPopup optionPopup;

    private ToggleGroup selectToggle;
    
    private final ToggleGroup searchSwitch = new ToggleGroup();
    
    public static MainController main;


    public MainController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        main = this;
    }

    @PostConstruct
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Center.setLabel(statusLabel);
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

        downloadTab.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Download (%s)", Downloader.getInstance().getDownloadList().size()),
                Downloader.getInstance().getDownloadList())
        );

        downloadSelectedButton.disableProperty().bind(Bindings.createBooleanBinding(() -> searchView.getSelectionModel().getSelectedItems().size() == 0, searchView.getSelectionModel().getSelectedItems()));
    }
    
    private void setUpRdToggle() {
        setUpSwitchButton(()->setUpKeywordToggle(), "keyword");
        setUpSwitchButton(()->setUpIdToggle(), "id");
        
        searchSwitch.selectedToggleProperty().addListener(
                event -> {
                        selectType.getChildren().clear();
                        selectToggle = new ToggleGroup();
                        ((Runnable)searchSwitch.getSelectedToggle().getUserData()).run(); 
                    });
        searchSwitch.selectToggle(searchSwitch.getToggles().get(0));
    }

    private void setUpIdToggle() {
        setUpRadioButton(new RunnableEvent.ArtistSearchEvent(), "artist");
        setUpRadioButton(new RunnableEvent.AlbumSearchEvent(), "album");
        setUpRadioButton(new RunnableEvent.SongSearchEvent(), "song");
        setUpRadioButton(new RunnableEvent.PlaylistSearchEvent(), "playlist");
        
        selectToggle.selectedToggleProperty().addListener(
                event -> Center.setUpIdValidationTextField(
                        ((ToggleData) selectToggle.getSelectedToggle().getUserData()).getData(), searchTextField));
        selectToggle.selectToggle(selectToggle.getToggles().get(0));
    }
    
    private void setUpKeywordToggle() {
        setUpRadioButton(new RunnableEvent.ArtistKWSearchEvent(), "artist");
        setUpRadioButton(new RunnableEvent.AlbumKWSearchEvent(), "album");
        setUpRadioButton(new RunnableEvent.SongKWSearchEvent(), "song");
        setUpRadioButton(new RunnableEvent.PlaylistKWSearchEvent(), "playlist");

        selectToggle.selectedToggleProperty().addListener(
                event -> Center.setUpKeywordTextField(searchTextField));
        selectToggle.selectToggle(selectToggle.getToggles().get(0));
    }

    private void setUpRadioButton(RunnableEvent event, String data) {
        JFXRadioButton radioButton = new JFXRadioButton(data.substring(0, 1).toUpperCase() + data.substring(1));
        radioButton.setPadding(new Insets(10));
        radioButton.setToggleGroup(selectToggle);
        radioButton.setUserData(new ToggleData(event, data));
        selectType.getChildren().add(radioButton);
    }
    
    private void setUpSwitchButton(Runnable event, String data) {
        JFXRadioButton radioButton = new JFXRadioButton(data.substring(0, 1).toUpperCase() + data.substring(1));
        radioButton.setPadding(new Insets(10));
        radioButton.setToggleGroup(searchSwitch);
        radioButton.setUserData(event);
        switchType.getChildren().add(radioButton);
    }

    @FXML
    public void search() {
        if (selectToggle.getSelectedToggle() != null && searchTextField.validate()) {
            // Start a new Thread to search in background
            String id = searchTextField.getText();
            searchTextField.clear();
            RunnableEvent event = ((ToggleData) selectToggle.getSelectedToggle().getUserData()).getEvent();
            ReadIDTask searchTask = new ReadIDTask(id, event);
            searchProgress.visibleProperty().bind(searchTask.runningProperty());
            ThreadUtils.startNormalThread(searchTask);
        }
    }

    private void initSearchView() {
        searchView.setEditable(false);
        searchView.setShowRoot(false);
        searchView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        setupViewColumns(Song.getColumns());
//        actionColumn.setCellFactory(param -> new TreeTableCell<Song, String>() {
//            @Override
//            protected void updateItem(String id, boolean empty) {
//                if (!empty) {
//                    JFXButton button = new JFXButton("Download");
//                    button.setStyle("-fx-text-fill:WHITE;-fx-background-color:#5264AE;-fx-font-size:14px;");
//                    button.setButtonType(JFXButton.ButtonType.RAISED);
//                    button.setOnAction(event -> ThreadUtils.startNormalThread(new ReadIDTask(id, new RunnableEvent.SongDownloadEvent())));
//                    setGraphic(button);
//                    setText("");
//                } else {
//                    setGraphic(null);
//                }
//            }
//        });

        searchFilterField.textProperty().addListener((observable, oldValue, newValue) ->
                searchView.setPredicate(e -> {
                    final Entity entity = e.getValue();
                    String filter = newValue.toLowerCase();
                    Map<String, StringProperty> map = entity.getProperties();
                    for(StringProperty sp : map.values()) {
                        if(sp.get().toLowerCase().contains(filter))
                            return true;
                    }
                    return false;
                }));
        selectionLabel.textProperty().bind(Bindings.createStringBinding(
                () -> searchView.getSelectionModel().getSelectedCells().size() + " selected",
                searchView.getSelectionModel().getSelectedItems()));

        Center.setSearchView(searchView);
        Center.setSearchListLabel(searchListLabel);
    }
    
    public void setupViewColumns(List<String> properties) {
        searchView.getColumns().clear();
        double width = Main.WIDTH / properties.size();
        properties.forEach(s->{
                JFXTreeTableColumn<Entity, String> column = new JFXTreeTableColumn<>(s);
                column.setPrefWidth(width);
                setupCellValueFactory(column, (Entity e)->e.getProperties().get(s));
                searchView.getColumns().add(column);
            });
    }

    private <T> void setupCellValueFactory(JFXTreeTableColumn<Entity, T> column, Function<Entity, ObservableValue<T>> mapper) {
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<Entity, T> param) -> {
            if (column.validateValue(param)) {
                return mapper.apply(param.getValue().getValue());
            } else {
                return column.getComputedValue(param);
            }
        });
    }

    @FXML
    public void downloadAll() {
        if (searchView.getRoot() == null)
            return;
        for (TreeItem<Entity> songTreeItem : searchView.getRoot().getChildren()) {
            try {
                ((Song)songTreeItem.getValue()).download();
            } catch (Exception e) {
                Center.printToStatus("What are you doing idiot?????");
            }
        }
    }

    @FXML
    public void downloadSelected() {
        for (TreeTablePosition<Entity, ?> cell : searchView.getSelectionModel().getSelectedCells()) {
            try {
                ((Song)cell.getTreeItem().getValue()).download();
            } catch (Exception e) {
                Center.printToStatus("What are you doing idiot?????");
            }
        }
    }

}
