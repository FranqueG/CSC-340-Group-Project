
package app;
import helloGui.CardViewController;
requires javafx.stage.Stage;
import javafx.stage.Stage;
import static helloApi.HelloApi.helloApi;
import static helloGui.HelloGui.helloGui;
public class Main {




    public static <Scene> void main(String[] args) throws Exception {
        System.out.println("Hello from main");
        helloApi();
        helloGui();
        new CardViewController().start();













    }






}
