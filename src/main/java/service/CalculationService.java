package service;

import model.CardModel;

public class CalculationService {

    private ValidateService validateService;

    public int[] calculateDamage(CardModel firstCard, CardModel secondCard) {
        int[] damageArray = new int[2];
        if (firstCard.getMonsterType() != null && secondCard.getMonsterType() != null) {
            damageArray[0] = validateService.validateMonsterType(firstCard.getMonsterType(), secondCard.getMonsterType(), firstCard.getDamage());
            damageArray[1] = validateService.validateMonsterType(secondCard.getMonsterType(), firstCard.getMonsterType(), secondCard.getDamage());
        } else {
            damageArray[0] = calculateNewDamage(validateService.validateType(firstCard.getElementType(), secondCard.getElementType(), firstCard.getMonsterType()), firstCard);
            damageArray[1] = calculateNewDamage(validateService.validateType(secondCard.getElementType(), firstCard.getElementType(), secondCard.getMonsterType()), secondCard);

        }
        return damageArray;
    }

    // double oder zwischen speichern
    public int calculateNewDamage(int validateType, CardModel card) {
        if (validateType == 3) {
            return  card.getDamage() / 2;
        }
        return validateType != 0 ? card.getDamage() * validateType : card.getDamage();
    }
}
