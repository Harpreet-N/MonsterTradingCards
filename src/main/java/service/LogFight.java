package service;

import model.CardModel;
import model.UserModel;
import org.apache.log4j.Logger;

public class LogFight {

    private static final Logger logger = Logger.getLogger(LogFight.class);

    public void printFightingResult(UserModel firstUser, UserModel secondUser, CardModel firstCard, CardModel secondCard, int firstDamage, int secondDamage) {
        logger.info("Player " + firstUser.getUsername() + ": " + firstCard.getName() + " (" + firstCard.getDamage() + ") vs "
                + "Player " + secondUser.getUsername() + ": " + secondCard.getName() + " (" + secondCard.getDamage() + ") => " +
                firstCard.getDamage() + " VS " + secondCard.getDamage() + " -> " + firstDamage + " VS " + secondDamage + " " +
                "=> " + firstCard.getName() + " defeats " + secondCard.getName());
    }

}
