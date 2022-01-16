package service;

import http.ResponseHandler;
import model.CardModel;
import model.UserModel;
import org.apache.log4j.Logger;

public class LogFightService {


    private static final Logger logger = Logger.getLogger(LogFightService.class);

    public void printFightingResult(UserModel firstUser, UserModel secondUser, CardModel firstCard, CardModel secondCard, double firstDamage, double secondDamage, ResponseHandler responseHandler) {
        logger.info("Player " + firstUser.getUsername() + ": " + firstCard.getId() + " (" + firstCard.getDamage() + ") vs "
                + "Player " + secondUser.getUsername() + ": " + secondCard.getId() + " (" + secondCard.getDamage() + ") => " +
                firstCard.getDamage() + " VS " + secondCard.getDamage() + " -> " + firstDamage + " VS " + secondDamage + " " +
                "=> " + firstCard.getId() + " defeats " + secondCard.getId());

        responseHandler.response("Player " + firstUser.getUsername() + ": " + firstCard.getId() + " (" + firstCard.getDamage() + ") vs "
                + "Player " + secondUser.getUsername() + ": " + secondCard.getId() + " (" + secondCard.getDamage() + ") => " +
                firstCard.getDamage() + " VS " + secondCard.getDamage() + " -> " + firstDamage + " VS " + secondDamage + " " +
                "=> " + firstCard.getId() + " defeats " + secondCard.getId());
    }
}
