package ca.bcit.cst.rongyi.ui;

import ca.bcit.cst.rongyi.util.Database;
import ca.bcit.cst.rongyi.util.Downloader;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.svg.SVGGlyph;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Main extends Application {

    public static final double WIDTH = 800.0;
    public static final double HEIGHT = 600.0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("/fxml/Main.fxml"));

        JFXDecorator decorator = new JFXDecorator(primaryStage, root);
        decorator.setCustomMaximize(true);
        decorator.setGraphic(new SVGGlyph(""));

        Scene scene = new Scene(decorator, WIDTH, HEIGHT);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(getClass().getResource("/css/jfoenix-fonts.css").toExternalForm(),
                getClass().getResource("/css/jfoenix-design.css").toExternalForm(),
                getClass().getResource("/css/jfoenix-main-demo.css").toExternalForm());

        primaryStage.setOnCloseRequest(event -> {
            for (File f : Downloader.TEMP_DIR.listFiles()) {
                f.delete();
            }
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(Database.OUTPUT));
                out.writeObject(Database.getInstance());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        primaryStage.setMinWidth(WIDTH);
        primaryStage.setMinHeight(HEIGHT);

        primaryStage.setScene(scene);
        primaryStage.setTitle("163Music");
        primaryStage.show();
    }
}
