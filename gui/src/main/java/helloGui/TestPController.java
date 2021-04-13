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
    public ComboBox<String> deckToAddToBox;
    public ComboBox<String> ruleCBox;
    public TextArea typeTxtArea;
    public TextArea nameTxtArea;
    public TextArea descriptionTxtArea;
    public TextField newDeckNameTxtField;
    public Button addTypeBtn;
    public Button clearTypeBtn;
    public Button createNewDeckBtn;
    public CheckBox greenY;
    public CheckBox whiteY;
    public CheckBox blackY;
    public CheckBox blueY;
    public CheckBox redY;
    public ListView resultsListView;
    public static ArrayList<Card> searchResultCards = new ArrayList<>();
    public static ArrayList<String> allCardTypes = getCardTypes();
    public static ArrayList<String> parameterCardTypes = new ArrayList<String>();
    public static ArrayList<String> pCT= new ArrayList<String>();
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
        //adds a type to search for
       String newType = cardTypeCBox.getValue();
        parameterCardTypes.add(newType );
        pCT.add(newType+ "\n");
       typeTxtArea.setText(" "+pCT.toString().replaceAll("[ \\[ \\] \\,]"," "));
    }
    public void clearTypeBtnClick(){
       //resets cardTypes to null
        parameterCardTypes.clear();
        pCT.clear();
       typeTxtArea.setText(" ");
    }


   public void searchBtnClick() {
       parameterDescription = descriptionTxtArea.getText();
       parameterName = nameTxtArea.getText();
       parameterColors = createColorString();
       parameterManaMin = (int) manaLow.getValue();
       parameterManaMax = (int) manaHigh.getValue();
     
       searchResultCards = advancedSearch(parameterName,parameterDescription,parameterCardTypes,parameterColors,parameterManaMin,parameterManaMax);
     
       resultsListView.setItems(FXCollections.observableList(searchResultCards));
       
   }
   public void createNewDeckBtnClick(){
        String deckName = newDeckNameTxtField.getText();
        String ruleSet = ruleCBox.getValue();

        //insertIntoDatabase(deckName,ruleSet){TODO}
        System.out.println("DN: "+deckName);
        System.out.println("RS: "+ruleSet);
        

   }
    public void addCardToDeckBtnClick(){
        String deckName = deckToAddToBox.getValue();
        String cardName = resultsListView.getSelectionModel().getSelectedItem().toString();

        int x = searchResultCards.size();
        for (int i = 0; i < x;i++){
            if (searchResultCards.get(i).toString().equals(cardName)){Card cardToAdd = searchResultCards.get(i); System.out.println("Found it!");}
        }
        //insertIntoDatabase(deckName,cardToAdd){stuff to do...}
        System.out.println("DN: "+deckName);
        System.out.println("CN: "+cardName);

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