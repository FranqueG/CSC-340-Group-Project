package shared;

import annotations.Column;
import annotations.Table;

import java.util.ArrayList;

@Table
public class Deck {
    @Column
    private String deckName;
    @Column
    private String ruleSet;
    @Column(unique = true)
    private Integer deckId;
    @Column
    private ArrayList<Card> cards;

    public Deck(String _name, String _ruleSet, Integer _deckId, ArrayList _cards) {
        this.deckName = _name;
        this.ruleSet = _ruleSet;
        this.deckId = _deckId;
        this.cards = _cards;
    }

    @Override
    public String toString() { return deckName; }
    public void addCards(Card _card) { cards.add(_card); }

    public ArrayList<Card> getCards() {
        return cards;
    }
}
