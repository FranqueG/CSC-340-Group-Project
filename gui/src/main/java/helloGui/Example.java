package helloGui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Example extends Application {

    @Override

    public void start(Stage primaryStage) throws Exception {

        var root = (AnchorPane) FXMLLoader.load(getClass().getResource("/GUIView.fxml"));
        var scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
