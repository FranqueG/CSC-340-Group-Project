package shared;

import annotations.Column;
import annotations.Table;

@Table
public class Card {
    @Column
    private String cardName;

    @Column(unique = true)
    private String cardId;

    @Override
    public String toString() {
        return cardName;
    }
}
