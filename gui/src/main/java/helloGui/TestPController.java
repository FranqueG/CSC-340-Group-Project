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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class TestPController  {
    @FXML
    public ImageView SearchPic;
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
    // These checkBoxes are for inclusion colors
    public CheckBox greenY;
    public CheckBox whiteY;
    public CheckBox blackY;
    public CheckBox blueY;
    public CheckBox redY;
    // these exclude colors
    public CheckBox greenN;
    public CheckBox whiteN;
    public CheckBox blackN;
    public CheckBox blueN;
    public CheckBox redN;
    public ListView resultsListView;
    // searchResultCards is an ArrayList of card results from the API
    public static ArrayList<Card> searchResultCards = new ArrayList<>();
    // allCardTypes is an ArrayList of types for the user to choose from
    public static ArrayList<String> allCardTypes = getCardTypes();
    // parameterCardTypes is an ArrayList formatted to display to the user
    public static ArrayList<String> parameterCardTypes = new ArrayList<String>();
    //pCT is an Arraylist formatted to work with the API
    public static ArrayList<String> pCT= new ArrayList<String>();
    //these are variables to store parameters to send to the API
    public static int parameterManaMin;
    public static int parameterManaMax;
    public static String parameterDescription;
    public static String parameterName;
    public static String parameterIncludeColors;
    public static String parameterExcludeColors;

    public Spinner manaLow;
    public Spinner manaHigh;


    // these are dummy cards... Card1 is used to return null values...
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

    public void showNewSearchPic() throws IOException {
        String cardName = resultsListView.getSelectionModel().getSelectedItem().toString();
        Card cardToAdd = getCardFromSearchResults(cardName);

        BufferedImage image = null;
        try {
            URL url = new URL(cardToAdd.getImage());
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            conn.connect();
            InputStream urlStream = conn.getInputStream();
            image = ImageIO.read(urlStream);
            if (image != null) {
                WritableImage wr = new WritableImage(image.getWidth(), image.getHeight());
                PixelWriter pw = wr.getPixelWriter();
                for (int x = 0; x < image.getWidth(); x++) {
                    for (int y = 0; y < image.getHeight(); y++) {
                        pw.setArgb(x, y, image.getRGB(x, y));
                    }
                }
                SearchPic.setImage(wr);
            }




        } catch (IOException e) {
            System.out.println("Something went wrong, sorry:" + e.toString());
            e.printStackTrace();
        }







    }

   public void searchBtnClick() {
       parameterDescription = descriptionTxtArea.getText();
       parameterName = nameTxtArea.getText();
       parameterIncludeColors = createColorString(greenY,redY,blackY,whiteY,blueY);
       parameterExcludeColors = createColorString(greenN,redN,blackN,whiteN,blueN);
       parameterManaMin = (int) manaLow.getValue();
       parameterManaMax = (int) manaHigh.getValue();
     
       searchResultCards = advancedSearch(parameterName,parameterDescription,parameterCardTypes,parameterIncludeColors,parameterExcludeColors,parameterManaMin,parameterManaMax);
     
       resultsListView.setItems(FXCollections.observableList(searchResultCards));
       
   }
   public void createNewDeckBtnClick(){
        String deckName = newDeckNameTxtField.getText();
        String ruleSet = ruleCBox.getValue();

        //insertIntoDatabase(deckName,ruleSet){TODO}
        System.out.println("DN: "+deckName);
        System.out.println("RS: "+ruleSet);
        

   }
   public Card getCardFromSearchResults(String cardName){
       int x = searchResultCards.size();
       for (int i = 0; i < x;i++){
           if (searchResultCards.get(i).toString().equals(cardName)){Card cardToAdd = searchResultCards.get(i); System.out.println("Found it!"+cardToAdd.getImage());return cardToAdd;}
       }

       return Card1;
   }
  //addCardToDeckBtnClick handles adding a card to a deck
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
    // this creates a string formatted for the API to include colors
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
}