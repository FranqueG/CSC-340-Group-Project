package helloGui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class CardViewController {


    public ListView CardList;
    public ImageView CardPic;
    public MenuBar MyMenu;
    public ScreenChanger MS = new ScreenChanger();
    public  void MC() throws Exception {

    }

    public void changeScreen(ActionEvent event) throws IOException {

        Parent tableViewParent = FXMLLoader.load(getClass().getResource("/CardView.fxml"));
        Scene tableViewScene = new Scene(tableViewParent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(tableViewScene);
        window.show();
    }

    public void launchDeckAdder(ActionEvent event) throws IOException{

        Stage stage = (Stage) MyMenu.getScene().getWindow();
        MS.changeScreenByStage(stage,"/DeckAdder.fxml");
    }
    public void launchDeckViewer(ActionEvent event) throws IOException{

        Stage stage = (Stage) MyMenu.getScene().getWindow();
        MS.changeScreenByStage(stage,"/DeckViewer.fxml");
    }
    public void launchCardSearch(ActionEvent event) throws IOException{

        Stage stage = (Stage) MyMenu.getScene().getWindow();
        MS.changeScreenByStage(stage,"/CardSearch.fxml");
    }
}
