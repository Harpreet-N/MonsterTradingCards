package model;

import model.helper.MonsterType;
import model.helper.Type;

public class Spell extends CardModel {

    public Spell(long id, String username, String packageId, Type elementType, MonsterType monsterType, double damage) {
        super.id = id;
        super.owner = username;
        super.packageId = packageId;
        super.elementType = elementType;
        super.monsterType = monsterType;
        super.damage = damage;
    }
}

