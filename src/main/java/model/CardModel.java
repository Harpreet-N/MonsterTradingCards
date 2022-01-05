package model;

import lombok.*;
import model.helper.MonsterType;
import model.helper.Type;

@ToString
@EqualsAndHashCode
public abstract class CardModel {

    @NonNull
    @Getter
    protected long id;

    @Getter
    protected String owner;
    @Getter
    protected String packageId;

    @Getter
    @Setter
    protected Type elementType;

    @Getter
    @Setter
    protected MonsterType monsterType;

    @Getter
    protected String storageType;

    @Getter
    @Setter
    protected double damage;
}
