package service;

import model.CardModel;
import model.battle.BattleResult;
import model.helper.MonsterType;
import model.helper.Type;

public class MonsterService {

    public static BattleResult getWinnerOfMonsterBattle(CardModel one, CardModel two) {
        String monsterNameOfCardOne = one.getMonsterType().name();
        String monsterNameOfCardTwo = two.getMonsterType().name();

        if (monsterNameOfCardOne.equals(MonsterType.GOBLIN.name()) && monsterNameOfCardTwo.equals(MonsterType.DRAGON.name())) {
            return new BattleResult(two, one);
        } else if (monsterNameOfCardOne.equals(MonsterType.DRAGON.name()) && monsterNameOfCardTwo.equals(MonsterType.GOBLIN.name())) {
            return new BattleResult(one, two);
        } else if (monsterNameOfCardOne.equals(MonsterType.WIZZARD.name()) && monsterNameOfCardTwo.equals(MonsterType.ORK.name())) {
            return new BattleResult(two, one);
        } else if (monsterNameOfCardOne.equals(MonsterType.ORK.name()) && monsterNameOfCardTwo.equals(MonsterType.WIZZARD.name())) {
            return new BattleResult(one, two);
        } else if ((one.getElementType().equals(Type.FIRE) && monsterNameOfCardOne.equals(MonsterType.FIREELVES.name()))
                && monsterNameOfCardTwo.equals(MonsterType.DRAGON.name())) {
            return new BattleResult(one, two);
        } else if (monsterNameOfCardOne.equals(MonsterType.DRAGON.name()) && (monsterNameOfCardTwo.equals(MonsterType.FIREELVES.name())) && two.getElementType().equals(Type.FIRE)) {
            return new BattleResult(two, one);
        } else {
            return damageCalculation(one, two);
        }
    }

    public static BattleResult damageCalculation(CardModel one, CardModel two) {
        if (one.getDamage() > two.getDamage()) {
            return new BattleResult(one, two);
        } else if (one.getDamage() < two.getDamage()) {
            return new BattleResult(two, one);
        } else {
            return null;
        }
    }

}
