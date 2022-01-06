package service;

import model.helper.MonsterType;
import model.helper.Type;

public class ValidateService {

    public int validateMonsterType(MonsterType firstType, MonsterType secondType, int damage) {
        if ((firstType == MonsterType.GOBLIN && secondType == MonsterType.DRAGON) ||
                firstType == MonsterType.ORK && secondType == MonsterType.WIZZARD ||
                (firstType == MonsterType.FIREELVES && secondType == MonsterType.DRAGON) ||
                (secondType == MonsterType.GOBLIN && firstType == MonsterType.DRAGON) ||
                secondType == MonsterType.ORK && firstType == MonsterType.WIZZARD ||
                (secondType == MonsterType.FIREELVES && firstType == MonsterType.DRAGON)) {
            return 0;
        }
        return damage;
    }

    public int validateType(Type firstType, Type secondType, MonsterType firstCard) {
        if (firstType == Type.WATER && secondType == Type.FIRE) {
            return 2;
        } else if (firstCard == MonsterType.KNIGHT && secondType == Type.WATER) {
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
