package helloGui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

import java.io.IOException;

public class ScreenChanger {
    public void changeScreen(ActionEvent event, String fxmlFileName) throws IOException {

        Parent tableViewParent = FXMLLoader.load(getClass().getResource(fxmlFileName));
        Scene tableViewScene = new Scene(tableViewParent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(tableViewScene);
        window.show();
    }
    public void changeScreenByStage(Stage MS, String fxmlFileName) throws IOException {

        Parent tableViewParent = FXMLLoader.load(getClass().getResource(fxmlFileName));
        Scene tableViewScene = new Scene(tableViewParent);

        MS.setScene(tableViewScene);
        MS.show();
    }
    public void changeScreenByMenu(MenuBar MB, String fxmlFileName) throws IOException {
        Stage MS = (Stage) MB.getScene().getWindow();
        Parent tableViewParent = FXMLLoader.load(getClass().getResource(fxmlFileName));
        Scene tableViewScene = new Scene(tableViewParent);

        MS.setScene(tableViewScene);
        MS.show();
    }



    }

