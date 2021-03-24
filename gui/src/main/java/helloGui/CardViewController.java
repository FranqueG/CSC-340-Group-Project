package helloGui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CardViewController {






    public  void start() throws Exception {

        Stage MS = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("CardView.fxml"));

        Scene scene = new Scene(root);

        MS.setScene(scene);
        MS.show();
    }
    public void changeScreen(ActionEvent event) throws IOException {

        Parent tableViewParent = FXMLLoader.load(getClass().getResource("CardView.fxml"));
        Scene tableViewScene = new Scene(tableViewParent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(tableViewScene);
        window.show();
    }

    public void MC(){}
}
