package ui;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.svg.SVGGlyph;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static final double WIDTH = 1024;
    public static final double HEIGHT = 768;

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
        stylesheets.addAll(
                getClass().getResource("/css/jfoenix-fonts.css").toExternalForm(),
                getClass().getResource("/css/jfoenix-design.css").toExternalForm(),
                getClass().getResource("/css/jfoenix-main-demo.css").toExternalForm());

        primaryStage.setOnCloseRequest(Center.CLOSE_EVENT);

        Center.setRootScene(scene);

        primaryStage.setMinWidth(WIDTH);
        primaryStage.setMinHeight(HEIGHT);
        primaryStage.setResizable(false);

        primaryStage.setScene(scene);
        primaryStage.setTitle("163Music");
        primaryStage.show();
    }
}
