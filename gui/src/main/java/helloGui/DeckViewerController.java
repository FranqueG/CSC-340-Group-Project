package helloGui;

import javafx.event.ActionEvent;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

import java.io.IOException;

public class DeckViewerController {
    public MenuBar MyMenu;
    public ScreenChanger MS = new ScreenChanger();
    public void launchDeckAdder(ActionEvent event) throws IOException {

        Stage stage = (Stage) MyMenu.getScene().getWindow();
        MS.changeScreenByStage(stage,"/DeckAdder.fxml");
    }
    public void launchDeckSearcher(ActionEvent event) throws IOException{

        Stage stage = (Stage) MyMenu.getScene().getWindow();
        MS.changeScreenByStage(stage,"/CardSearch.fxml");
    }
}
