package model;

import lombok.*;
import model.helper.Type;

import java.util.ArrayList;
import java.util.List;

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
     *  The type of a card can be a spell or a monster
     *  Is the cardTypeMonster is true than the type of the card is a monster
     *  If it is false it is a spell
     */
    @Getter
    @Setter
    private boolean cardTypeMonster;

    @Getter
    @Setter
    private Type cardType;

}
