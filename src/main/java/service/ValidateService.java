package service;

import model.helper.MonsterType;
import model.helper.Type;

public class ValidateService {

    public int validateMonsterType(MonsterType firstType, MonsterType secondType, int damage) {
        if ((firstType == MonsterType.GOBLINS && secondType == MonsterType.DRAGONS) ||
                firstType == MonsterType.ORKS && secondType == MonsterType.WIZZARD ||
                (firstType == MonsterType.FIREELVES && secondType == MonsterType.DRAGONS) ||
                (secondType == MonsterType.GOBLINS && firstType == MonsterType.DRAGONS) ||
                secondType == MonsterType.ORKS && firstType == MonsterType.WIZZARD ||
                (secondType == MonsterType.FIREELVES && firstType == MonsterType.DRAGONS)) {
            return 0;
        }
        return damage;
    }

    public int validateType(Type firstType, Type secondType, MonsterType firstCard) {
        if (firstType == Type.WATER && secondType == Type.FIRE) {
            return 2;
        } else if (firstCard == MonsterType.KNIGHTS && secondType == Type.WATER) {
            return 0;
        } else if (firstCard == MonsterType.KRAKEN) {
            return 1;
        } else if (firstType == Type.FIRE && secondType == Type.NORMAL) {
            return 2;
        } else if (firstType == Type.NORMAL && secondType == Type.WATER) {
            return 2;
        } else if (firstType == Type.FIRE && secondType == Type.WATER) {
            return 3;
        }
        return 0;
    }

}
