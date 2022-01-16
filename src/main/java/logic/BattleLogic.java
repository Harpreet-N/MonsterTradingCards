package logic;

import database.Database;
import database.DatabaseUser;
import http.ResponseHandler;
import model.CardModel;
import model.UserModel;
import model.battle.BattleResult;
import org.apache.log4j.Logger;
import service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BattleLogic {

    private static final Logger logger = Logger.getLogger(BattleLogic.class);

    private final Database dbA;
    private DatabaseUser db;

    private final ConcurrentLinkedQueue<UserModel> battleQueue = new ConcurrentLinkedQueue<>();
    private ResponseHandler responseHandler;
    private BattleResult battle;
    private CalculationService calculationService = new CalculationService();
    private LogFightService logFightService = new LogFightService();
    private DeckService deckService = new DeckService();
    private RandomService randomService = new RandomService();


    public BattleLogic(Database dbA) {
        this.dbA = dbA;
        db = new DatabaseUser(dbA.getStmt(), dbA.getConnection());
    }

    public synchronized void queueFighter(ResponseHandler rph, String username) {
        UserModel userModel = db.getUserData(username);
        battleQueue.add(userModel);
        responseHandler = rph;

        if ((battleQueue.size() % 2 == 0) && (!battleQueue.isEmpty())) {
            UserModel usernameOne = battleQueue.poll();
            UserModel usernameTwo = battleQueue.poll();

            logAndRespond(" vs ", usernameOne.getUsername(), "Battle: ", usernameTwo.getUsername(), rph);
            UserModel userModel1 = startTheGame(usernameOne, usernameTwo, rph);

            if (userModel1 != null) {
                db.editUser(usernameOne);
                db.editUser(usernameTwo);
            }
        }

        logger.info(userModel.getUsername() + " added to battle queue!");
        rph.response(userModel.getUsername() + " added to battle queue!");
    }


    private UserModel startTheGame(UserModel firstUser, UserModel secondUser, ResponseHandler rph) {
        List<CardModel> deckOne = new ArrayList<>(db.getDeck(firstUser.getUsername()));
        List<CardModel> deckTwo = new ArrayList<>(db.getDeck(secondUser.getUsername()));

        for (int i = 0; i < 100; i++) {
            if (deckOne.isEmpty()) {
                logger.info("User: " + secondUser.getUsername() + " won");
                evaluateWinAndLostResult(secondUser.getUsername(), firstUser.getUsername());
                return secondUser;
            } else if (deckTwo.isEmpty()) {
                logger.info("User: " + firstUser.getUsername() + " won");
                evaluateWinAndLostResult(firstUser.getUsername(), secondUser.getUsername());
                return firstUser;
            } else {
                CardModel cardModelFirst = randomService.getRandomCardFromDeck(deckOne);
                CardModel cardModelSecond = randomService.getRandomCardFromDeck(deckTwo);
                if (cardModelFirst != null && cardModelSecond != null) {
                    specialRound(cardModelFirst, cardModelSecond, i);
                    fight(firstUser, secondUser, cardModelFirst, cardModelSecond, rph, deckOne, deckTwo);
                } else {
                    UserModel winner = cardModelFirst == null ? secondUser : firstUser;
                    logger.info("User: " + winner.getUsername() + " won");
                    uniqueFeature(i, winner, secondUser);
                    return winner;
                }
            }
        }
        logger.info("Nobody won");
        uniqueFeature(100, firstUser, secondUser);
        return null;
    }

    private void specialRound(CardModel cardModelFirst, CardModel cardModelSecond, int roundCount) {
        if (roundCount == 25 || roundCount == 50 || roundCount == 75) {
            randomService.setRandomDamage(cardModelFirst, cardModelSecond);
        }
    }

    private void uniqueFeature(int i, UserModel firstUser, UserModel secondUser) {
        if (i == 5) {
            firstUser.setElo(firstUser.getElo() + 20);
        } else if (i == 99) {
            firstUser.setElo(firstUser.getElo() + 10);
            secondUser.setElo(secondUser.getElo() + 10);
        } else if (i == 100) {
            firstUser.setElo(firstUser.getElo() + 5);
            secondUser.setElo(secondUser.getElo() + 5);
        }
    }

    private void fight(UserModel firstUser, UserModel secondUser, CardModel firstCard, CardModel secondCard, ResponseHandler rph, List<CardModel> deckOne, List<CardModel> deckTwo) {
        double[] result = calculationService.calculateDamage(firstCard, secondCard);
        double damageResult = result[0] - result[1];

        if (damageResult > 0) {
            logFightService.printFightingResult(firstUser, secondUser, firstCard, secondCard, result[0], result[1], rph);
            deckService.removeCardFromUser(deckOne, deckTwo, secondCard);
        } else if (damageResult != 0) {
            deckService.removeCardFromUser(deckTwo, deckOne, firstCard);
            logFightService.printFightingResult(secondUser, firstUser, secondCard, firstCard, result[0], result[1], rph);

        }
    }

    private void logAndRespond(String cardOne, String elementTypeOne, String battleEndMessage, String s, ResponseHandler responseHandler) {
        logger.info(battleEndMessage + elementTypeOne + cardOne + s);
        responseHandler.response(battleEndMessage + elementTypeOne + cardOne + s);
    }

    private void evaluateWinAndLostResult(String win, String loser) {
        db.addWin(win);
        db.addLoss(loser);
    }
}