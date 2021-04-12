package helloGui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.ArrayList;

public class TestPController  {
    @FXML
    private ComboBox<String> box;

    @FXML
    private void initialize() {
        var strings = new ArrayList<String>();
        strings.add("test1");
        strings.add("test2");
        strings.add("test3");
        box.setItems(FXCollections.observableList(strings));
    }
}
