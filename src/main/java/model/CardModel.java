package model;

import lombok.*;
import model.helper.MonsterType;
import model.helper.Type;

@Data
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

    @Getter
    @Setter
    private Type elementType;

    @Getter
    @Setter
    private MonsterType monsterType;

    @Getter
    @Setter
    private StackModel stackModel;

    @Getter
    @Setter
    private Deck deck;

}
