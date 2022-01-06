package model;

import model.helper.MonsterType;
import model.helper.Type;

public class Spell extends CardModel {

    public Spell(String id, String username, String packageId, Type elementType, MonsterType monsterType, double damage) {
        super.id = id;
        super.owner = username;
        super.packageId = packageId;
        super.elementType = elementType;
        super.monsterType = monsterType;
        super.damage = damage;
    }


    public static boolean elementTypeIsEffective(Type one, Type two) {
        if (one.equals(Type.FIRE) && two.equals(Type.WATER)) {
            return false;
        } else if (one.equals(Type.WATER) && two.equals(Type.FIRE)) {
            return true;
        } else if (one.equals(Type.NORMAL) && two.equals(Type.FIRE)) {
            return false;
        } else if (one.equals(Type.FIRE) && two.equals(Type.NORMAL)) {
            return true;
        } else if (one.equals(Type.NORMAL) && two.equals(Type.WATER)) {
            return true;
        } else if (one.equals(Type.WATER) && two.equals(Type.NORMAL)) {
            return false;
        }

        return false;
    }
}

