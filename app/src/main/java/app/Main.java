
package app;
//import helloGui.CardViewController;


import gui.GUI;
import javafx.application.Application;
import manager.DatabaseManager;


public class Main {




    public static void main(String[] args) throws Exception {
        System.out.println("Hello from main");

        DatabaseManager.connectToDatabase();

        Application.launch(GUI.class, args);
//        fillTypeComboBox();

    }


}
