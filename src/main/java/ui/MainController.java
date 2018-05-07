package ui;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXRippler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import util.Downloader;

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
    private JFXHamburger titleBurger;

    @FXML
    private JFXRippler titleRippler;

    @FXML
    private JFXHamburger optionBurger;

    @FXML
    private JFXRippler optionRippler;

    private JFXPopup downloadPopup;

    private JFXPopup optionPopup;

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

        try {
            downloadPopup = new JFXPopup(FXMLLoader.load(getClass().getResource("/fxml/ui/DownloadPopup.fxml")));
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
        }
        titleBurger.setOnMouseClicked(event -> {
            downloadPopup.show(titleRippler, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, 50.0, 10.0);
        });

        try {
            optionPopup = new JFXPopup(FXMLLoader.load(getClass().getResource("/fxml/ui/OptionPopup.fxml")));
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
        }
        optionBurger.setOnMouseClicked(event -> {
            optionPopup.show(optionRippler, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT, -50.0, 10.0);
        });

    }

}
