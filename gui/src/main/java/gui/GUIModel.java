package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import manager.DatabaseManager;
import shared.Card;
import shared.Deck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static api.Api.getCardTypes;
import static gui.GUIController.*;

public class GUIModel {
    @FXML
    // Picture of selected card.
    public ImageView SearchPic;
    // Picture of selected card in specific deck
    public ImageView CardInDeckPic;
    // List of types for user to choose from.
    public ComboBox<String> cardTypeCBox;
    // List of decks in database for user to choose from
    public ComboBox<Deck> deckToAddToBox;
    // List of rule sets for user to choose from.
    public ComboBox<String> ruleCBox;
    // List of decks in database that the user can remove
    public ComboBox<Deck> deckCBox;
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
    public ListView deckDisplay;
    public ListView deckCardDisplay;

    // allCardTypes is an ArrayList of types for the user to choose from.
    public static ArrayList<String> allCardTypes = getCardTypes();

    // decks is an ArrayList of decks that the user can choose from
    public static ArrayList<Deck> decks = new ArrayList<>();

    //ArrayList<Card> cards = new ArrayList<>();

    public Deck Deck1 = new Deck(null, null, null);

    // these are dummy cards... Card1 is used to return null values...
    public Card Card1 = new Card("","Card1","","",1,"","","");
    public Card Card2 = new Card(null,null,null,null,1,null,null,null);


    @FXML
    //initialize fills cardTypeCBox with types for the user to choose from.
    public void initialize() {
        try {
            deckDisplay.setItems(FXCollections.observableList(DatabaseManager.loadObject(new Deck()).get()));
            deckCBox.setItems(FXCollections.observableList(DatabaseManager.loadObject(new Deck()).get()));
            deckToAddToBox.setItems(FXCollections.observableList(DatabaseManager.loadObject(new Deck()).get()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        cardTypeCBox.setItems(FXCollections.observableList(allCardTypes));
        deckCBox.setItems(deckDisplay.getItems());
        deckToAddToBox.setItems(deckDisplay.getItems());
    }


    //addTypeBtnClick adds a type to search for
    public void addTypeBtnClick(){

        String newType = cardTypeCBox.getValue().trim();
        typeTxtArea.setText(" "+addToTypeArray(newType).toString().replaceAll("[ \\[ \\] \\,]"," "));

    }
    //clearTypeBtnClick resets cardTypes to null
    public void clearTypeBtnClick(){

        clearCardTypeArray();
        typeTxtArea.setText(" ");

    }

    // addNewDeck creates a new deck and saves it to the database
    public void addNewDeck() throws ExecutionException, InterruptedException {
        decks = DatabaseManager.loadObject(new Deck()).get();
        Deck newDeck = new Deck(newDeckNameTxtField.getText().trim(), ruleCBox.getValue(),null);
        for (var deck : decks) {
            if(deck.getDeckName().equals(newDeck.getDeckName())) {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid deck name");
                alert.setHeaderText("A deck with named "+newDeck.getDeckName()+" already exists!");
                alert.setContentText("Please chose a different name for your deck.");
                alert.show();
                return;
            }
        }
        decks.add(newDeck);
        DatabaseManager.saveObjects(decks);
        deckDisplay.getItems().add(newDeck);
        deckCBox.setItems(deckDisplay.getItems());
        deckToAddToBox.setItems(deckDisplay.getItems());
    }

    // removeDeck removes an existing deck and updates the database
    public void removeDeck() throws ExecutionException, InterruptedException {
        Deck currentDeck = deckCBox.getValue();
        Deck NewDeck = new Deck();
        if (currentDeck != null) {
            NewDeck.setDeckName(currentDeck.getDeckName());
            DatabaseManager.deleteObject(NewDeck);
            deckDisplay.getItems().remove(deckCBox.getValue());
        }
    }

    public void addNewCard() {
        Card cardToAdd = Card1;
        Deck currentDeck = deckToAddToBox.getValue();
        if (currentDeck.getCards() == null){
            currentDeck.setCards(new ArrayList<>());
        }
        var cards = currentDeck.getCards();
        String cardName = resultsListView.getSelectionModel().getSelectedItem().toString();
        int x = searchResultCards.size();
        for (int i = 0; i < x;i++){
            if (searchResultCards.get(i).toString().equals(cardName)){cardToAdd = searchResultCards.get(i); System.out.println("Found it!");}
        }

        cards.add(cardToAdd);
        DatabaseManager.saveObject(currentDeck);

    }

    public void removeCard(){
        Deck currentDeck = (Deck) deckDisplay.getSelectionModel().getSelectedItem();
        Card selectedCard = (Card) deckCardDisplay.getSelectionModel().getSelectedItem();
        if (currentDeck != null && selectedCard != null) {
            currentDeck.removeCards(selectedCard);
            DatabaseManager.deleteObject(selectedCard);
            deckCardDisplay.getItems().remove(selectedCard);
        }
        deckCardDisplay.refresh();

    }

    public void displayCardsInDeck(){
        deckCardDisplay.getItems().removeAll();
        Deck currentDeck = (Deck) deckDisplay.getSelectionModel().getSelectedItem();
        if (currentDeck != null && currentDeck.getCards() != null) {
            ObservableList<Card> list = FXCollections.observableList(currentDeck.getCards());
            deckCardDisplay.setItems(list);
        }
        deckCardDisplay.refresh();
    }

    public void showCardPic() throws IOException{
        Card cardToShow = (Card) deckCardDisplay.getSelectionModel().getSelectedItem();
        if (cardToShow != null) {
            WritableImage wr = getWritableImageFromURL(cardToShow);
            CardInDeckPic.setImage(wr);
        }
    }

    // showNewSearchPic changes the card image displayed to the user
    public void showNewSearchPic() throws IOException {
        Card cardToShow = (Card) resultsListView.getSelectionModel().getSelectedItem();
        WritableImage wr = getWritableImageFromURL(cardToShow);
        SearchPic.setImage(wr);
        }
   // searchBtnClick returns a new arraylist of card results, then sets resultsListView to display them
   public void searchBtnClick() {

       String _parameterDescription = descriptionTxtArea.getText().trim();
       String  _parameterName = nameTxtArea.getText().trim();
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