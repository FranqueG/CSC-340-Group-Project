package api;

import org.json.JSONArray;
import org.json.JSONObject;
import shared.Card;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
public class Api {

    public static String baseURL = "https://api.scryfall.com";

    /**
     * This method separates a string by spaces into words and inserts '+' between them
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
     * This method gets all of the available card types for the user to filter
     *
     * @return
     */
    public static ArrayList getCardTypes() {
        // Initialize array list
        ArrayList<String> types = new ArrayList<>();

        // This string will start out as "creature - " and append the type name to the end
        String baseCardType = "";
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
            try{
                // Parse into JSON object.
                JSONObject json = new JSONObject(connect(urlString));
                // Put json data into the cardType array list
                JSONArray array = json.getJSONArray("data");
                for (int j = 0; j < array.length(); j++) {
                    String specificType = baseCardType + array.getString(j);
                    types.add(specificType);
                }
            } catch (Exception exception) {
                System.out.println("Error: " + exception);
                return null;
            }
        }
        return types;
    }

    /**
     * Returns the list of all rulesets for the user to select from
     * @return
     */
    public static ArrayList getRuleSets() {
        ArrayList<String> rulesets = new ArrayList<>();
        rulesets.add("Standard");
        rulesets.add("Modern");
        rulesets.add("Legacy");
        rulesets.add("Commander");

        return rulesets;
    }

    /**
     * Constructs a url string based on the user's search preferences
     *
     * @param _name
     * @param _type
     * @param _color
     * @param _maxCMC
     * @param _minCMC
     * @return
     */
    public static ArrayList advancedSearch(String _name, String _description, ArrayList<String> _types, String _includeColors, String _excludeColors, int _minCMC, int _maxCMC) {
        String callAction = "/cards/search?q=";
        String urlString = baseURL + callAction;

        // This array list is used to dynamically append parameters to the urlString
        ArrayList<String> parameters = new ArrayList<>();
        // Dynamically append each filter to the url parameter list
        addName(parameters, _name);
        addDescription(parameters, _description);
        addTypes(parameters, _types);
        addColors(parameters, _includeColors, _excludeColors);
        addMana(parameters, _minCMC, _maxCMC);

        // Construct final urlString from modified parameter list
        for (int i = 0; i < parameters.size(); i++) {
            urlString += parameters.get(i);
            // Append '+' signs between words but not at the end
            if (i != parameters.size() - 1) {
                urlString += "+";
            }
        }
        System.out.println(urlString);
        // Get filtered card search from the api
        ArrayList<Card> cards = new ArrayList<>();
        getCardList(urlString, cards);

        // Return list of cards for later use
        return cards;
    }

    /**
     * Adds the card name field to the list of all url parameters
     *
     * @param _params
     * @param _name
     */
    private static void addName(ArrayList<String> _parameters, String _name) {
        // Construct nameString for a word-specific search query
        if (_name != null && _name.isEmpty() != true) {
            // Separate string by spaces in search term
            String nameString = "";
            nameString = separateBySpaces(_name, nameString);
            // Append name string to parameter array list
            _parameters.add(nameString);
        }
    }

    /**
     * Adds the card description field to the list of all url parameters
     *
     * @param _parameters
     * @param _description
     */
    private static void addDescription(ArrayList<String> _parameters, String _description) {
        if (_description != null && _description.isEmpty() != true) {
            // Separate string by spaces in search term
            String descriptionString = "%28";
            descriptionString = separateBySpaces(_description, descriptionString, "oracle%3A");
            descriptionString += "%29";
            // Append name string to parameter array list
            _parameters.add(descriptionString);
        }
    }

    /**
     * Adds card types field to url parameter list
     *
     * @param _parameters
     * @param _types
     */
    private static void addTypes(ArrayList<String> _parameters, ArrayList<String> _types) {
        // Sort through non-empty list of card types that the user selected
        if (_types.isEmpty() != true) {
            // Append parenthesis code
            String temp;
            String typeString = "%28t%3A";
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
            _parameters.add(typeString);
        }
    }

    /**
     * Adds color filter to url parammeter list
     *
     * @param _parameters
     * @param _includeColor
     * @param _excludeColor
     */
    private static void addColors(ArrayList<String> _parameters, String _includeColor, String _excludeColor) {
        // Construct colorString. The Strings _color and _excludeColor are in GRUWB format with each letter associating to a color to filter by.
        if (_includeColor != null && _includeColor.isEmpty() != true) {
            String colorString = "color%3C%3D";
            colorString += _includeColor;

            // Add excluded colors if applicable
            if (_excludeColor != null && _excludeColor.isEmpty() != true) {
                colorString += "++";
                char[] ch = _excludeColor.toCharArray();

                for (int i = 0; i < ch.length; i++) {
                    colorString += "%2Dc%3A" + ch[i];
                    if (i != ch.length - 1) {
                        colorString += "+";
                    }
                }
            }
            _parameters.add(colorString);
        }
    }

    /**
     * Adds mana cost range to url parameter list
     *
     * @param _parameters
     * @param _minCMC
     * @param _maxCMC
     */
    private static void addMana(ArrayList<String> _parameters, int _minCMC, int _maxCMC) {
        if (_maxCMC >= _minCMC) {
            String manaString = "cmc%3C%3D" + _maxCMC + "+cmc%3E%3D" + _minCMC;
            _parameters.add(manaString);
        }
    }

    /**
     * This method is for requesting json data and putting it into an array
     * list. It will iterate through every available page of the specific query
     * (page results are limited to 175 entries each)
     * @param _urlString
     * @param _cards
     */
    private static void getCardList(String _urlString, ArrayList _cards) {
        final String IMAGE_SIZE = "normal";

        // Data used to control looping mechanism
        String nextPage = _urlString;
        boolean hasNext = false;
        URL url;

        // Do-While loop to get all search content to be sorted
        do {
            try{
                // Parse into JSON object.
                JSONObject json = new JSONObject(connect(_urlString));
                JSONArray array = json.getJSONArray("data");
                // Put json data into array list of card objects
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
                    _cards.add(new Card(id, cardName, cardType, manaCost, convertedManaCost, cardText, cardRarity, imageURI));

                    // Get the page link for the next request if it exists
                    if (json.has("next_page")) {
                        nextPage = json.getString("next_page");
                    }
                    // Get next_page condition
                    hasNext = json.getBoolean("has_more");
                }
            } catch (Exception exception) {
                System.out.println("Error: " + exception);
            }

        } while (hasNext);
    }

    /**
     * This method makes an api request and returns the JSON string for other methods to create JSON Objects from
     * @param _urlString
     * @return
     */
    private static String connect(String _urlString){
        String jsonString = "";

        URL url;
        try {
            // Make the connection.
            url = new URL(_urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // Examine response code.
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                if (responseCode == 404) {
                    System.out.println("Error: No data found from request: " + responseCode);
                } else {
                    System.out.println("Error: Could not load information: " + responseCode);
                }
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
                jsonString = content.toString();
            }
        } catch (Exception exception) {
            System.out.println("Error: " + exception);
            return null;
        }
        return jsonString;
    }

}