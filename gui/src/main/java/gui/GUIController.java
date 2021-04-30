package gui;

import javafx.scene.control.ListView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import shared.Card;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static api.Api.advancedSearch;
import static api.Api.getCardTypes;
public class GUIController {
    public ListView resultsListView;
    // searchResultCards is an ArrayList of card results from the API.
    public static ArrayList<Card> searchResultCards = new ArrayList<>();
    // allCardTypes is an ArrayList of types for the user to choose from.
    public static ArrayList<String> allCardTypes = getCardTypes();
    // parameterCardTypes is an ArrayList formatted to work with the API.
    public static ArrayList<String> _parameterCardTypes = new ArrayList<String>();
    //pCT is an Arraylist formatted to display to the user.
    public static ArrayList<String> pCT= new ArrayList<String>();

    // these are dummy cards... Card1 is used to return null values...
    public static Card Card1 = new Card("","Card1","","",1,"","","");
    public Card Card2 = new Card("","Card2","","",1,"","","");

    // clearCardTypeArray clears the running list of user chosen card types to search for
    public static void clearCardTypeArray(){

        _parameterCardTypes.clear();
        pCT.clear();

        }
    // addToTypeArray adds a type to the running list of user chosen card types to search for
    public static ArrayList<String> addToTypeArray(String newType){

        _parameterCardTypes.add(newType );
        pCT.add(newType+ "\n");
        return pCT;

        }
        // perform search calls the API to get an Arraylist of results
    public static ArrayList<Card> performSearch (String _parameterName,String _parameterDescription,String _parameterIncludeColors,String _parameterExcludeColors,int _parameterManaMin,int _parameterManaMax){
        searchResultCards = advancedSearch(_parameterName,_parameterDescription,_parameterCardTypes,_parameterIncludeColors,_parameterExcludeColors,_parameterManaMin,_parameterManaMax);
        return searchResultCards;
        }

    //getWritableImageFromURL uses the URL String from a card object to return a writable image from the internet
    public static WritableImage getWritableImageFromURL(Card _cardToShow){

        BufferedImage image = null;
        try {
            //get URL using String from card object
            URL url = new URL(_cardToShow.getImage());
            //open connection
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.connect();
            // get and read URL from input stream
            InputStream urlStream = conn.getInputStream();
            // create and write image from  stream
            image = ImageIO.read(urlStream);
            // create buffered image by getting image size, then looping through each pixel and setting the buffered image equal to the standard image
            if (image != null) {
                WritableImage wr = new WritableImage(image.getWidth(), image.getHeight());
                PixelWriter pw = wr.getPixelWriter();
                for (int x = 0; x < image.getWidth(); x++) {
                    for (int y = 0; y < image.getHeight(); y++) {
                        pw.setArgb(x, y, image.getRGB(x, y));
                    }
                }
                return wr;
            }




        } catch (IOException e) {
            System.out.println("Something went wrong, sorry:" + e.toString());
            e.printStackTrace();
        }
        // create a null writable image, in case the above code couldn't execute
        WritableImage nullWR = new WritableImage(0, 0);
        return nullWR;
    }

     // searches the arraylist of card results for a specific card based on name
    public static Card getCardFromSearchResults(String cardName){
        int x = searchResultCards.size();
        for (int i = 0; i < x;i++){
            if (searchResultCards.get(i).toString().equals(cardName)){
                Card cardToAdd = searchResultCards.get(i);
                System.out.println("Found it!"+cardToAdd.getImage());
                return cardToAdd;
            }
        }

        return Card1;
    }

}
