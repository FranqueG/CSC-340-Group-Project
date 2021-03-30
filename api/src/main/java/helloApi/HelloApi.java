package helloApi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import helloApi.*;

public class HelloApi {
    public static void helloApi() {
        System.out.println("Hello from api");


    }

    public static String baseURL = "https://api.scryfall.com";

    /**
     * This method
     *
     * @param _inputStr
     * @param _outputStr
     * @return
     */
    private static String separateBySpaces(String _inputStr, String _outputStr) {
        String[] splitName = _inputStr.split("\\s+");
        for (int i = 0; i < splitName.length; i++) {
            _outputStr += splitName[i];
            // Adds '+' in between each word until last word added.
            if (i != splitName.length - 1) {
                _outputStr += "+";
            }
        }
        return _outputStr;
    }

    /**
     * Overloaded method to append certain criteria between each word
     *
     * @param _inputStr
     * @param _outputStr
     * @param modifier
     * @return
     */
    private static String separateBySpaces(String _inputStr, String _outputStr, String modifier) {
        String[] splitName = _inputStr.split("\\s+");
        for (int i = 0; i < splitName.length; i++) {
            _outputStr = _outputStr + modifier + splitName[i];
            // Adds '+' in between each word until last word added.
            if (i != splitName.length - 1) {
                _outputStr += "+";
            }
        }
        return _outputStr;
    }

    /**
     * This method gets all of the available card types for the user to filter by
     *
     * @return
     */
    public static ArrayList getCardTypes() {
        // Initialize array list
        ArrayList<String> types = new ArrayList<>();

        // This string will start out as "creature - " and append the type name to the end
        String baseCardType = "";
        String specificType = "";
        String callAction = "/catalog";
        String finalAction = "";

        types.add("Type - Instant");
        // Modify the url string and card type name for each type value
        for (int i = 1; i < 7; i++) {
            switch (i) {
                case 1:
                    types.add("Creature - Creature");
                    finalAction = "/creature-types";
                    baseCardType = "Creature - ";
                    break;
                case 2:
                    types.add("Land - Land");
                    finalAction = "/land-types";
                    baseCardType = "Land - ";
                    break;
                case 3:
                    types.add("Planeswalker - Planeswalker");
                    finalAction = "/planeswalker-types";
                    baseCardType = "Planeswalker - ";
                    break;
                case 4:
                    types.add("Artifact - Artifact");
                    finalAction = "/artifact-types";
                    baseCardType = "Artifact - ";
                    break;
                case 5:
                    finalAction = "/enchantment-types";
                    baseCardType = "Enchantment - ";
                    break;
                case 6:
                    types.add("Spell - Spell");
                    finalAction = "/spell-types";
                    baseCardType = "Spell - ";
                    break;
            }

            // Construct url
            String urlString = baseURL + callAction + finalAction;
            URL url;
            try {
                // Make the connection.
                url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                // Examine response code.
                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    System.out.println("Error: Could not load card information: " + responseCode);
                } else {
                    // Parsing input stream into a text string.
                    BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuffer content = new StringBuffer();
                    while ((inputLine = input.readLine()) != null) {
                        content.append(inputLine);
                    }
                    // Close connections.
                    input.close();
                    connection.disconnect();
                    // Parse into JSON object.
                    JSONObject json = new JSONObject(content.toString());
                    // Put json data into the cardType array list
                    JSONArray array = json.getJSONArray("data");
                    for (int j = 0; j < array.length(); j++) {
                        specificType = baseCardType + array.getString(j);
                        types.add(specificType);
                    }
                }
            } catch (Exception exception) {
                System.out.println("Error: " + exception);
                return null;
            }
        }
        // Return the list of card types
        return types;
    }

    /**
     * constructs a url string based on the user's search preferences
     * Note: The String _color is in GRUWB format with each letter associating to a color to filter by.
     *
     * @param _name
     * @param _description
     * @param _type
     * @param _color
     * @param _minCMC
     * @param _maxCMC
     * @return
     */
    public static ArrayList advancedSearch(String _name, String _description, ArrayList<String> _types, String _color, int _minCMC, int _maxCMC) {
        String callAction = "/cards";
        String secondAction = "/search?q=";
        String nameString = "";
        String descriptionString = "%28";
        String typeString = "%28t%3A";
        String colorString = "color%3C%3D";
        String manaString = null;
        String urlString = baseURL + callAction + secondAction;

        // Create array list to dynamically append parameters to the urlString
        ArrayList<String> parameters = new ArrayList<>();

        // Construct nameString for a word-specific search query
        if (_name != null && _name.isEmpty() != true) {
            // Separate string by spaces in search term
            nameString = separateBySpaces(_name, nameString);
            // Append name string to parameter array list
            parameters.add(nameString);
        }

        // Construct descriptionString for a word-specific search query
        if (_description != null && _description.isEmpty() != true) {
            // Separate string by spaces in search term
            descriptionString = separateBySpaces(_description, descriptionString, "oracle%3A");
            descriptionString += "%29";
            // Append name string to parameter array list
            parameters.add(descriptionString);
        }

        // Sort through non-empty list of card types that the user selected
        if (_types.isEmpty() != true) {
            // Append parenthesis code
            String temp = null;
            for (int i = 0; i < _types.size(); i++) {
                // Split string at hyphen
                temp = _types.get(i);
                String[] splitStr = temp.split(" - ");
                typeString += splitStr[1];
                // Append '+' signs between words but not at the end
                if (i != _types.size() - 1) {
                    typeString += "+t%3A";
                }
            }
            // Append parenthesis code and type string to parameter array list
            typeString += "%29";
            parameters.add(typeString);
        }

        // Construct colorString.
        if (_color != null && _color.isEmpty() != true) {
            colorString += _color;
            parameters.add(colorString);
        }

        // Construct manaString from given minimum and maximum mana cost
        if (_maxCMC >= _minCMC) {
            manaString = "cmc%3C%3D" + _maxCMC + "+cmc%3E%3D" + _minCMC;
            parameters.add(manaString);
        }

        // Construct final urlString from modified parameters
        for (int i = 0; i < parameters.size(); i++) {
            urlString += parameters.get(i);
            // Append '+' signs between words but not at the end
            if (i != parameters.size() - 1) {
                urlString += "+";
            }
        }

        // Get filtered card search from the api
        ArrayList<Card> cards = getCardList(urlString);

        // Return list of cards for later use
        return cards;
    }

    /**
     * MODIFIED AT LINE: 290-325 to get more data for card objects (For prototyping) --> Implement Card class
     *
     * This method is for requesting json data and putting it into an array list.
     * It will iterate through every available page of the specific query and put requested cards objects into a list
     * (page results are limited to 175 entries each)
     * @param _urlString
     * @return
     */
    private static ArrayList getCardList(String _urlString) {
        final String IMAGE_SIZE = "normal";

        // Data used to control looping mechanism
        String nextPage = _urlString;
        boolean hasNext = false;
        URL url;

        // Initialize array list to return
        ArrayList<Card> cards = new ArrayList<>();

        // Do-While loop to get all search content to be sorted
        do {
            try {
                // Make the connection.
                url = new URL(nextPage);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Examine response code.
                int responseCode = connection.getResponseCode();

                if (responseCode != 200) {
                    System.out.println("Error: Could not load card information: " + responseCode);
                } else {
                    // Parsing input stream into a text string.
                    BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuffer content = new StringBuffer();
                    while ((inputLine = input.readLine()) != null) {
                        content.append(inputLine);
                    }
                    // Close connections.
                    input.close();
                    connection.disconnect();

                    // Parse into JSON object.
                    JSONObject json = new JSONObject(content.toString());

                    // Parse JSONArray for card data
                    JSONArray array = json.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        // Get card data on consistent fields
                        String id = array.getJSONObject(i).getString("id");
                        String cardName = array.getJSONObject(i).getString("name");
                        String cardRarity = array.getJSONObject(i).getString("rarity");
                        String cardType = array.getJSONObject(i).getString("type_line");

                        // Check if image uri exists
                        String imageURI = "";
                        if (array.getJSONObject(i).has("image_uris")) {
                            imageURI = array.getJSONObject(i).getJSONObject("image_uris").getString(IMAGE_SIZE);
                        }

                        // Check if mana_cost field exists since some cards are manaless.
                        String manaCost = "";
                        if (array.getJSONObject(i).has("mana_cost")) {
                            manaCost = array.getJSONObject(i).getString("mana_cost");
                        } else {
                            manaCost = "0";
                        }

                        // Check if converted mana cost field exists
                        int convertedManaCost;
                        if (array.getJSONObject(i).has("cmc")) {
                            convertedManaCost = array.getJSONObject(i).getInt("cmc");
                        } else {
                            convertedManaCost = 0;
                        }

                        // Check if description text exists since some cards have no description.
                        String cardText = "";
                        if (array.getJSONObject(i).has("oracle_text")) {
                            cardText = array.getJSONObject(i).getString("oracle_text");
                        } else {
                            cardText = "No description";
                        }

                        // Add card object to array list
                        cards.add(new Card(id, cardName, cardType, manaCost, convertedManaCost, cardText, cardRarity, imageURI));

                        // Get the page link for the next request if it exists
                        if (json.has("next_page")) {
                            nextPage = json.getString("next_page");
                        }
                        // Get next_page condition
                        hasNext = json.getBoolean("has_more");
                    }
                }
            } catch (Exception exception) {
                System.out.println("Error: " + exception);
            }
            // Continue to next page if it exists
        } while (hasNext);
        // Return the card objects
        return cards;
    }


    }
