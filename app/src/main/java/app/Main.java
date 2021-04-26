
package app;
//import helloGui.CardViewController;


import helloGui.Example;
import javafx.application.Application;


import static helloApi.HelloApi.helloApi;
import static helloGui.HelloGui.helloGui;
//import static helloGui.TestPController.fillTypeComboBox;
public class Main {




    public static void main(String[] args) throws Exception {
        System.out.println("Hello from main");
        helloApi();
        helloGui();

        Application.launch(Example.class, args);
//        fillTypeComboBox();

    }


}
