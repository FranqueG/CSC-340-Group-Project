package helloGui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Spinner;
import javafx.stage.Stage;
import helloGui.ScreenChanger.*;
import java.io.IOException;



public class CardSearchController {

    public MenuBar MyMenu;
    public ScreenChanger MS = new ScreenChanger();
    public Spinner manaLow = new Spinner();

    public void launchCardView(ActionEvent event) throws IOException {

        MS.changeScreen(event,"/TestP.fxml");

    }
    public void launchDeckAdder() throws IOException{


        MS.changeScreenByMenu(MyMenu,"/DeckAdder.fxml");
    }
    public void launchDeckViewer() throws IOException{


        MS.changeScreenByMenu(MyMenu,"/DeckViewer.fxml");
}}
