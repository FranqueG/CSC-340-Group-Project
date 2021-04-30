package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import manager.DatabaseManager;

import java.util.Objects;

public class GUI extends Application {

    @Override

    public void start(Stage primaryStage) throws Exception {

        var root = (AnchorPane) FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/GUIView.fxml")));
        var scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest((arg) -> DatabaseManager.shutdownDatabase());
        primaryStage.show();
    }


}
