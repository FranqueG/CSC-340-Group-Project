package helloGui;

import javafx.event.ActionEvent;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

import java.io.IOException;

public class DeckAdderController {
    public MenuBar MyMenu;
    public ScreenChanger MS = new ScreenChanger();

    public void launchDeckViewer(ActionEvent event) throws IOException {

        Stage stage = (Stage) MyMenu.getScene().getWindow();
        MS.changeScreenByStage(stage,"/DeckViewer.fxml");
    }
    public void launchDeckSearcher(ActionEvent event) throws IOException{

        Stage stage = (Stage) MyMenu.getScene().getWindow();
        MS.changeScreenByStage(stage,"/CardSearch.fxml");
    }

}
