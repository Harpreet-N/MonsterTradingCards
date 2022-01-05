package model;

import model.helper.MonsterType;
import model.helper.Type;

public class Monster extends CardModel {

    public Monster(long id, String username, String packageId, Type elementType, MonsterType monsterType, double damage) {
        super.id = id;
        super.owner = username;
        super.packageId = packageId;
        super.elementType = elementType;
        super.monsterType = monsterType;
        super.damage = damage;
    }

}
