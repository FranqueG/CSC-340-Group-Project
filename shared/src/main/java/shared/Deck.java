package shared;

import annotations.Column;
import annotations.Table;

import java.util.ArrayList;

@Table(name = "Deck")
public class Deck {
    @Column(unique = true)
    private String deckName;
    @Column
    private String ruleSet;
    @Column(containsType = Card.class)
    private ArrayList<Card> cards;

    public Deck(String _name, String _ruleSet, ArrayList<Card> _cards) {
        this.deckName = _name;
        this.ruleSet = _ruleSet;
        this.cards = _cards;
    }

    /**
     * Empty constructor is necessary for the database to instantiate it
     */
    public Deck() {}

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public String getRuleSet() {
        return ruleSet;
    }

    public void setRuleSet(String ruleSet) {
        this.ruleSet = ruleSet;
    }

    @Override
    public String toString() { return deckName; }
    public void addCards(Card _card) { this.cards.add(_card); }
    public ArrayList<Card> getCards() { return cards; }
    public void setCards(ArrayList<Card> _cards){ this.cards = _cards; }
    public void removeCards(Card _card) { this.cards.remove(_card); }
}