package model;

import lombok.*;
import model.helper.Type;


@AllArgsConstructor
public class CardModel {

    @NonNull
    private long cardId;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private int damage;

    /**
     * The type of a card can be a spell or a monster
     * Is the cardTypeMonster is true than the type of the card is a monster
     * If it is false it is a spell
     */
    @Getter
    @Setter
    private boolean cardTypeMonster;

    @Getter
    @Setter
    private Type elementType;

    @Getter
    @Setter
    private StackModel stackModel;

    @Getter
    @Setter
    private Deck deck;

}
