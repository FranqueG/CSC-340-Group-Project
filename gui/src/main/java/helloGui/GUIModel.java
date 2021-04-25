package helloGui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import shared.Card;


import javax.imageio.ImageIO;
import javax.swing.*;

import static helloApi.HelloApi.getCardTypes;
import static helloApi.HelloApi.advancedSearch;
import static helloGui.GUIController.clearCardTypeArray;

import static helloGui.GUIController.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class GUIModel {
    @FXML
    // Picture of selected card.
    public ImageView SearchPic;
    // List of types for user to choose from.
    public ComboBox<String> cardTypeCBox;
    // List of decks in database for user to choose from
    public ComboBox<String> deckToAddToBox;
    // List of rule sets for user to choose from.
    public ComboBox<String> ruleCBox;
    // A text area to display chosen types.
    public TextArea typeTxtArea;
    // A text area for the user to type a card name to search for.
    public TextArea nameTxtArea;
    // A text area for the user to type a card description to search for.
    public TextArea descriptionTxtArea;
    // A text field for the user to type a new deck name
    public TextField newDeckNameTxtField;
    // Button to add a chosen type to search for
    public Button addTypeBtn;
    // Button to clear the types to search for
    public Button clearTypeBtn;
    // Button to insert a new deck into the database.
    public Button createNewDeckBtn;
    // These checkBoxes are for inclusion colors.
    public CheckBox greenY;
    public CheckBox whiteY;
    public CheckBox blackY;
    public CheckBox blueY;
    public CheckBox redY;
    // These exclude colors.
    public CheckBox greenN;
    public CheckBox whiteN;
    public CheckBox blackN;
    public CheckBox blueN;
    public CheckBox redN;
    // Spinners to get mana costs.
    public Spinner manaLow;
    public Spinner manaHigh;
    // A List View to show card results for the user to choose from.
    public ListView resultsListView;

    // allCardTypes is an ArrayList of types for the user to choose from.
    public static ArrayList<String> allCardTypes = getCardTypes();




    // these are dummy cards... Card1 is used to return null values...
    public Card Card1 = new Card("","Card1","","",1,"","","");
    public Card Card2 = new Card("","Card2","","",1,"","","");

    @FXML
    //initialize fills cardTypeCBox with types for the user to choose from.
    public void initialize() {

        cardTypeCBox.setItems(FXCollections.observableList(allCardTypes));
    }

    //addTypeBtnClick adds a type to search for
    public void addTypeBtnClick(){

        String newType = cardTypeCBox.getValue();
        typeTxtArea.setText(" "+addToTypeArray(newType).toString().replaceAll("[ \\[ \\] \\,]"," "));

    }
    //clearTypeBtnClick resets cardTypes to null
    public void clearTypeBtnClick(){

        clearCardTypeArray();
        typeTxtArea.setText(" ");

        }
    // showNewSearchPic changes the card image displayed to the user
    public void showNewSearchPic() throws IOException {
        String cardName = resultsListView.getSelectionModel().getSelectedItem().toString();
        Card cardToShow = getCardFromSearchResults(cardName);
        WritableImage wr = getWritableImageFromURL(cardToShow);
        SearchPic.setImage(wr);
        }
   // searchBtnClick returns a new arraylist of card results, then sets resultsListView to display them
   public void searchBtnClick() {

       String _parameterDescription = descriptionTxtArea.getText();
       String  _parameterName = nameTxtArea.getText();
       String _parameterIncludeColors = createColorString(greenY,redY,blackY,whiteY,blueY);
       String _parameterExcludeColors = createColorString(greenN,redN,blackN,whiteN,blueN);
       int _parameterManaMin = (int) manaLow.getValue();
       int _parameterManaMax = (int) manaHigh.getValue();
       ArrayList<Card> mySearchResultCards  = performSearch(_parameterName,_parameterDescription,_parameterIncludeColors,_parameterExcludeColors,_parameterManaMin,_parameterManaMax);
       resultsListView.setItems(FXCollections.observableList(mySearchResultCards));
       
       }
   // createColorString formats a string from checkboxes the API can use to search cards with
    public String createColorString(CheckBox G,CheckBox R, CheckBox U, CheckBox W, CheckBox B){
        String colorString = "";
        if (G.isSelected()){colorString += "G";}
        if (R.isSelected()){colorString += "R";}
        if (U.isSelected()){colorString += "U";}
        if (W.isSelected()){colorString += "W";}
        if (B.isSelected()){colorString += "B";}
        System.out.println(colorString);
        return colorString;

    }



   //JUNK!!!!!! USE LATER??? JUST DELETE????

   //public void createNewDeckBtnClick(){
        //String deckName = newDeckNameTxtField.getText();
        //String ruleSet = ruleCBox.getValue();

        //insertIntoDatabase(deckName,ruleSet){TODO}
      //  System.out.println("DN: "+deckName);
    //    System.out.println("RS: "+ruleSet);
        

  // }
   //public Card getCardFromSearchResults(String cardName){
     //  int x = searchResultCards.size();
     //  for (int i = 0; i < x;i++){
      //     if (searchResultCards.get(i).toString().equals(cardName)){Card cardToAdd = searchResultCards.get(i); System.out.println("Found it!"+cardToAdd.getImage());return cardToAdd;}
     //  }

   //   return Card1;
   //}
  //addCardToDeckBtnClick handles adding a card to a deck
   // public void addCardToDeckBtnClick(){
     //   String deckName = deckToAddToBox.getValue();
       // String cardName = resultsListView.getSelectionModel().getSelectedItem().toString();

        //int x = searchResultCards.size();
       // for (int i = 0; i < x;i++){
        //    if (searchResultCards.get(i).toString().equals(cardName)){Card cardToAdd = searchResultCards.get(i); System.out.println("Found it!");}
       // }
        //insertIntoDatabase(deckName,cardToAdd){stuff to do...}
       // System.out.println("DN: "+deckName);
       // System.out.println("CN: "+cardName);

    //}




    // this creates a string formatted for the API to include colors

}