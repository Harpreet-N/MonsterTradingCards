package run;

import model.CardModel;
import model.UserModel;
import org.apache.log4j.Logger;
import service.CalculationService;
import service.DeckService;
import service.LogFight;


public class BattelLogic {

    private static final Logger logger = Logger.getLogger(BattelLogic.class);

    private CalculationService calculationService;

    private LogFight logFight;

    private DeckService deckService;
/*
    public UserModel startTheGame(UserModel firstUser, UserModel secondUser) {
        for (int i = 0; i < 100; i++) {
            CardModel cardModelFirst = deckService.getCardModel(firstUser);
            CardModel cardModelSecond = deckService.getCardModel(secondUser);
            if (cardModelFirst != null && cardModelSecond != null) {
                fight(firstUser, secondUser, cardModelFirst, cardModelSecond);
            } else {
                UserModel winner = cardModelFirst == null ? secondUser : firstUser;
                logger.info("User: " + winner.getUsername() + " won");
                return winner;
            }
        }
        logger.info("Nobody won");
        return null;
    }


    private void fight(UserModel firstUser, UserModel secondUser, CardModel firstCard, CardModel secondCard) {
        int[] result = calculationService.calculateDamage(firstCard, secondCard);
        int damageResult = result[0] - result[1];

        if (damageResult > 0) {
            logFight.printFightingResult(firstUser, secondUser, firstCard, secondCard, result[0], result[1]);
            deckService.removeCardFromUser(firstUser, secondUser, secondCard);
        } else if (damageResult != 0) {
            deckService.removeCardFromUser(secondUser, firstUser, firstCard);
            logFight.printFightingResult(secondUser, firstUser, secondCard, firstCard, result[0], result[1]);

        }
    } */
}
