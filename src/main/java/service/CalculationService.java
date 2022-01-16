package service;

import model.CardModel;
import model.helper.MonsterType;

public class CalculationService {

    private ValidateService validateService = new ValidateService();

    public double[] calculateDamage(CardModel firstCard, CardModel secondCard) {
        double[] damageArray = new double[2];
        if (!(firstCard.getMonsterType().equals(MonsterType.SPELL)) && !(secondCard.getMonsterType().equals(MonsterType.SPELL))) {
            damageArray[0] = validateService.validateMonsterType(firstCard.getMonsterType(), secondCard.getMonsterType(), firstCard.getDamage());
            damageArray[1] = validateService.validateMonsterType(secondCard.getMonsterType(), firstCard.getMonsterType(), secondCard.getDamage());
        } else {
            damageArray[0] = calculateNewDamage(validateService.validateType(firstCard.getElementType(), secondCard.getElementType(), firstCard.getMonsterType()), firstCard);
            damageArray[1] = calculateNewDamage(validateService.validateType(secondCard.getElementType(), firstCard.getElementType(), secondCard.getMonsterType()), secondCard);

        }
        return damageArray;
    }

    public double calculateNewDamage(double validateType, CardModel card) {
        if (validateType == 3) {
            return  card.getDamage() / 2;
        }
        return validateType != 0 ? card.getDamage() * validateType : card.getDamage();
    }
}
