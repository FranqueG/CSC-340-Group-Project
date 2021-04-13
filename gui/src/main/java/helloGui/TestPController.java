package helloGui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import shared.Card;


import static helloApi.HelloApi.getCardTypes;
import static helloApi.HelloApi.advancedSearch;
import java.util.ArrayList;

public class TestPController  {
    @FXML
    public ComboBox<String> cardTypeCBox;
    public TextArea typeTxtArea;
    public TextArea nameTxtArea;
    public TextArea descriptionTxtArea;
    public Button addTypeBtn;
    public Button clearTypeBtn;
    public CheckBox greenY;
    public CheckBox whiteY;
    public CheckBox blackY;
    public CheckBox blueY;
    public CheckBox redY;
    public ListView resultsListView;
    public static ArrayList<Card> searchResultCards = new ArrayList<>();
    public static ArrayList<String> allCardTypes = getCardTypes();
    public static ArrayList<String> parameterCardTypes = new ArrayList<String>();
    public static String parameterDescription;
    public static String parameterName;
    public static String parameterColors;
    public Spinner manaLow;
    public Spinner manaHigh;
    public static int parameterManaMin;
    public static int parameterManaMax;
public Card Card1 = new Card("","Card1","","",1,"","","");
    public Card Card2 = new Card("","Card2","","",1,"","","");

    @FXML
    public void initialize() {
        var strings = new ArrayList<String>();
       // var oL = getCardTypes();
        strings.add("test1");
        strings.add("test2");
        strings.add("test3");
        System.out.println(allCardTypes.toString());
       // System.out.println(oL.toString());
        cardTypeCBox.setItems(FXCollections.observableList(allCardTypes));
    }


    public void addTypeBtnClick(){
       String newType = cardTypeCBox.getValue();
        parameterCardTypes.add(newType + "\n");
       typeTxtArea.setText(" "+parameterCardTypes.toString().replaceAll("[ \\[ \\] \\,]"," "));
    }
    public void clearTypeBtnClick(){
       
        parameterCardTypes.clear();
       typeTxtArea.setText(" ");
    }


   public ArrayList searchBtnClick() {
       parameterDescription = descriptionTxtArea.getText();
       parameterName = nameTxtArea.getText();
       parameterColors = createColorString();
       parameterManaMin = (int) manaLow.getValue();
       parameterManaMax = (int) manaHigh.getValue();
      // searchResultCards = advancedSearch(parameterName,parameterDescription,parameterCardTypes,parameterColors,parameterManaMin,parameterManaMax);
       searchResultCards.add(Card1);
       searchResultCards.add(Card2);
       resultsListView.setItems(FXCollections.observableList(searchResultCards));
       return searchResultCards;
   }
   public String createColorString(){
       String colorString = "";
        if (greenY.isSelected()){colorString += "G";}
       if (redY.isSelected()){colorString += "R";}
       if (blueY.isSelected()){colorString += "U";}
       if (whiteY.isSelected()){colorString += "W";}
       if (blackY.isSelected()){colorString += "B";}
       System.out.println(colorString);
       return colorString;

   }
}