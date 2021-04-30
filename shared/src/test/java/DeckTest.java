import manager.DatabaseManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import shared.Card;
import shared.Deck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

public class DeckTest {

    @BeforeAll
    public static void setup() throws IOException {
        DatabaseManager.connectToDatabase();
        DatabaseManager.testClearDatabase(Deck.class);
    }


    /**
     * This shows that database operations on deck objects work
     * @throws ExecutionException if loading fails
     * @throws InterruptedException if loading is interrupted
     */
    @Test
    public void deckTest() throws ExecutionException, InterruptedException {
        var cards = new ArrayList<Card>();
        cards.add(new Card("A","Bla","test","nothing",0,"This is a test","very common","blank"));
        cards.add(new Card("B","Bla","test","nothing",0,"This is a test","very common","blank"));
        cards.add(new Card("C","Bla","test","nothing",0,"This is also a test","very common","blank"));
        Deck deck = new Deck("Deck1","bla",cards);
        DatabaseManager.saveObject(deck);

        var future = DatabaseManager.loadObject(new Deck()); // Load all decks
        var deckResult = future.get().get(0);

        assert (deck.toString().equals(deckResult.toString()));

        var results = deck.getCards();
        cards.sort(Comparator.comparing(Card::toString));
        results.sort(Comparator.comparing(Card::toString));

        for(int i=0;i<cards.size();i++) {
            assert (cards.get(i).equals(results.get(i)));
        }
    }

}
