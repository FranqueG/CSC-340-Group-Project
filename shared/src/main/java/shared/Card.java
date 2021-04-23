package shared;

import annotations.Column;
import annotations.Table;

@Table
public class Card {
    @Column
    private String cardName;
    @Column
    private String image;
    @Column(unique = true)
    private String cardId;

    public Card(String _id, String _name,String _cardType, String _manaCost, int _convertedManaCost, String _cardText, String _cardRarity, String _image) {
        this.cardId = _id;
        this.cardName = _name;
        this.image = _image;
    }

    @Override
    public String toString() {
        return cardName;
    }
    public String getImage() {return image;}
}
