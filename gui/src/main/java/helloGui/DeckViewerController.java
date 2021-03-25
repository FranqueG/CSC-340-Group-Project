package helloGui;

import javafx.event.ActionEvent;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

import java.io.IOException;

public class DeckViewerController {
    public MenuBar MyMenu;
    public ScreenChanger MS = new ScreenChanger();
    public void launchDeckAdder(ActionEvent event) throws IOException {

        MS.changeScreenByMenu(MyMenu,"/DeckAdder.fxml");
    }
    public void launchDeckSearcher(ActionEvent event) throws IOException{

        MS.changeScreenByMenu(MyMenu,"/CardSearch.fxml");
    }
}
