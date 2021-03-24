package app;

import static helloApi.HelloApi.helloApi;
import static helloGui.HelloGui.helloGui;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello from main");
        helloApi();
        helloGui();
    }
}
