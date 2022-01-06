package model;

import model.battle.Battle;
import model.helper.MonsterType;
import model.helper.Type;

public class Monster extends CardModel {

    public Monster(String id, String username, String packageId, Type elementType, MonsterType monsterType, double damage) {
        super.id = id;
        super.owner = username;
        super.packageId = packageId;
        super.elementType = elementType;
        super.monsterType = monsterType;
        super.damage = damage;
    }


}