package helloGui;

import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class CardViewController {


    public ListView CardList;
    public ImageView CardPic;
    public MenuBar MyMenu;
    public ScreenChanger MS = new ScreenChanger();
    public  void MC() throws Exception {

    }



    public void launchDeckAdder() throws IOException{

        MS.changeScreenByMenu(MyMenu,"/DeckAdder.fxml");
    }
    public void launchDeckViewer() throws IOException{

        MS.changeScreenByMenu(MyMenu,"/DeckViewer.fxml");
    }
    public void launchCardSearch() throws IOException{

        MS.changeScreenByMenu(MyMenu,"/CardSearch.fxml");
    }
}
