package shared;

import annotations.Column;
import annotations.Table;

import java.util.Objects;

@Table(name = "Card")
public class Card {
    @Column
    private String cardName;
    @Column
    private String image;
    @Column(unique = true)
    private String cardId;
    @Column
    private String type;
    @Column
    private String manaCost;
    @Column
    private Integer convertedManaCost;
    @Column
    private String text;
    @Column
    private String rarity;


    public Card(String _id, String _name,String _cardType, String _manaCost, int _convertedManaCost, String _cardText, String _cardRarity, String _image) {
        this.cardId = _id;
        this.cardName = _name;
        this.image = _image;
        this.type = _cardType;
    }

    public Card() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(cardId, card.cardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId);
    }

    @Override
    public String toString() {
        return cardName;
    }
    public String getImage() {return image;}
}
