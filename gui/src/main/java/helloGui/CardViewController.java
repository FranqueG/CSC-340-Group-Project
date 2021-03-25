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



    public void launchDeckAdder(ActionEvent event) throws IOException{

        MS.changeScreenByMenu(MyMenu,"/DeckAdder.fxml");
    }
    public void launchDeckViewer(ActionEvent event) throws IOException{

        MS.changeScreenByMenu(MyMenu,"/DeckViewer.fxml");
    }
    public void launchCardSearch(ActionEvent event) throws IOException{

        MS.changeScreenByMenu(MyMenu,"/CardSearch.fxml");
    }
}
