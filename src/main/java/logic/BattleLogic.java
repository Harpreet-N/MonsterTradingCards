package logic;

import database.Database;
import database.DatabaseUser;
import http.ResponseHandler;
import model.CardModel;
import model.battle.BattleResult;
import model.helper.MonsterType;
import model.helper.Type;
import org.apache.log4j.Logger;
import service.ElementService;
import service.MonsterService;
import service.RandomService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BattleLogic {

    private static final Logger logger = Logger.getLogger(BattleLogic.class);
    private final Database dbA;
    private DatabaseUser db;
    private final ConcurrentLinkedQueue<String> battleQueue = new ConcurrentLinkedQueue<>();
    private MonsterService monsterService;
    private BattleResult battle;
    private ResponseHandler responseHandler;


    public BattleLogic(Database dbA) {
        this.dbA = dbA;
        db = new DatabaseUser(dbA.getStmt(), dbA.getConnection());
    }

    public synchronized void queueFighter(ResponseHandler rph, String username) {
        battleQueue.add(username);
        responseHandler = rph;

        if ((battleQueue.size() % 2 == 0) && (!battleQueue.isEmpty())) {
            String usernameOne = battleQueue.poll();
            String usernameTwo = battleQueue.poll();

            logAndRespond(" vs ", usernameOne, "Battle: ", usernameTwo, rph);
            startBattle(usernameOne, usernameTwo);
        }

        logger.info(username + " added to battle queue!");
        rph.response(username + " added to battle queue!");
    }

    private void startBattle(String usernameOne, String usernameTwo) {
        List<CardModel> deckOne = new ArrayList<>(db.getDeck(usernameOne));
        List<CardModel> deckTwo = new ArrayList<>(db.getDeck(usernameTwo));

        for (int i = 0; i < 100; i++) {
            if (deckTwo.isEmpty()) {
                logger.info(usernameOne + " won!");
                responseHandler.response(usernameOne + " won!");
                return;
            } else if (deckOne.isEmpty()) {
                logger.info(usernameTwo + " won!");
                responseHandler.response(usernameTwo + " won!");
                return;
            } else {
                // Get Random Card 
                CardModel randomCardFromUserO = RandomService.pickRandomCardFromDeck(deckOne);
                CardModel randomCardFromUserT = RandomService.pickRandomCardFromDeck(deckTwo);

                String cardOne = randomCardFromUserO.getMonsterType().name();
                String cardTwo = randomCardFromUserT.getMonsterType().name();

                String elementTypeOne = randomCardFromUserO.getElementType().name();
                String elementTypeTwo = randomCardFromUserT.getElementType().name();

                if (!cardOne.equals(MonsterType.SPELL.name()) && !cardTwo.equals(MonsterType.SPELL.name())) {
                    battle = MonsterService.getWinnerOfMonsterBattle(randomCardFromUserO, randomCardFromUserT);

                    String battleEndMessage = usernameOne + ": " + elementTypeOne + cardOne
                            + " (" + randomCardFromUserO.getDamage() + " Damage) vs "
                            + usernameTwo + ": " + elementTypeTwo + cardTwo
                            + " (" + randomCardFromUserT.getDamage() + " Damage) => ";

                    evaluateBattleResult(usernameOne, usernameTwo, deckOne, deckTwo, randomCardFromUserO, randomCardFromUserT, battle, battleEndMessage);
                } else {
                    double damageOne = randomCardFromUserO.getDamage();
                    double damageTwo = randomCardFromUserT.getDamage();

                    String battleEndMessage = usernameOne + ": " + elementTypeOne + cardOne + " (" +
                            randomCardFromUserO.getDamage() + " Damage) vs " +
                            usernameTwo + ": " + elementTypeTwo + cardTwo + " (" +
                            randomCardFromUserT.getDamage() + " Damage) => " + randomCardFromUserO.getDamage() + " vs " +
                            randomCardFromUserT.getDamage() +
                            " -> " + damageOne + " vs " + damageTwo + " => ";


                    if (cardOne.equals(MonsterType.KNIGHT.name()) && elementTypeTwo.equals(Type.WATER.name())) {
                        fightingResult(usernameOne, usernameTwo, deckOne, deckTwo, randomCardFromUserO, randomCardFromUserT, battleEndMessage);
                    } else {
                        if (ElementService.elementTypeIsEffective(randomCardFromUserO.getElementType(), randomCardFromUserT.getElementType())) {
                            // Fire vs Water
                            damageOne = damageOne * 2;
                            damageTwo = damageTwo / 2;
                        } else if (ElementService.elementTypeIsEffective(randomCardFromUserT.getElementType(), randomCardFromUserO.getElementType())) {
                            damageOne = damageOne / 2;
                            damageTwo = damageTwo * 2;
                        }

                        battleEndMessage = usernameOne + ": " + elementTypeOne + cardOne + " (" + randomCardFromUserO.getDamage() + " Damage) vs " +
                                usernameTwo + ": " + elementTypeTwo + cardTwo + " (" + randomCardFromUserT.getDamage() + " Damage) => " + randomCardFromUserO.getDamage() + " vs " + randomCardFromUserT.getDamage() +
                                " -> " + damageOne + " vs " + damageTwo + " => ";

                        if (damageOne > damageTwo) {
                            // One is winnerCard
                            saveDbAndTradeCard(usernameOne, usernameTwo, deckOne, deckTwo, randomCardFromUserT);
                            logAndRespond(cardOne, elementTypeOne, battleEndMessage, " win", responseHandler);

                        } else if (damageTwo > damageOne) {
                            // Two is winnerCard
                            saveDbAndTradeCard(usernameTwo, usernameOne, deckTwo, deckOne, randomCardFromUserO);
                            logAndRespond(cardTwo, elementTypeTwo, battleEndMessage, " win", responseHandler);
                        } else {
                            logger.info(battleEndMessage + "Draw");
                            responseHandler.response(battleEndMessage + "Draw");
                        }
                    }
                }
            }
        }
        logger.info("Battle was a draw!");
    }

    private void logAndRespond(String cardOne, String elementTypeOne, String battleEndMessage, String s, ResponseHandler responseHandler) {
        logger.info(battleEndMessage + elementTypeOne + cardOne + s);
        responseHandler.response(battleEndMessage + elementTypeOne + cardOne + s);
    }

    private void fightingResult(String usernameOne, String usernameTwo, List<CardModel> deckOne, List<CardModel> deckTwo, CardModel randomCardFromUserO, CardModel randomCardFromUserT, String battleEndMessage) {
        db.addWin(usernameTwo);
        db.addLoss(usernameOne);
        this.tradeCard(deckTwo, deckOne, randomCardFromUserO);

        logger.info(battleEndMessage);
        responseHandler.response(battleEndMessage);
    }

    private void evaluateBattleResult(String usernameOne, String usernameTwo, List<CardModel> deckOne, List<CardModel> deckTwo, CardModel randomCardFromUserO, CardModel randomCardFromUserT, BattleResult battle, String battleEndMessage) {
        if (battle != null) {
            CardModel winner = battle.getWinner();
            CardModel looser = battle.getLooser();
            if (winner.equals(randomCardFromUserO)) {
                saveDbAndTradeCard(usernameOne, usernameTwo, deckOne, deckTwo, randomCardFromUserT);
            } else {
                saveDbAndTradeCard(usernameTwo, usernameOne, deckTwo, deckOne, randomCardFromUserO);
            }
            logger.info(battleEndMessage + winner.getElementType().name() + winner.getMonsterType().name() + " defeats " + looser.getElementType().name() + looser.getMonsterType().name());
            responseHandler.response(battleEndMessage + winner.getElementType().name() + winner.getMonsterType().name() + " defeats " + looser.getElementType().name() + looser.getMonsterType().name());
        } else {
            logger.info(battleEndMessage + "Draw");
            responseHandler.response(battleEndMessage + "Draw");
        }
    }

    private void saveDbAndTradeCard(String winUser, String loseUser, List<CardModel> winnerDeck, List<CardModel> looserDeck, CardModel randomCardFromLoser) {
        db.addWin(winUser);
        db.addLoss(loseUser);
        this.tradeCard(winnerDeck, looserDeck, randomCardFromLoser);
    }

    private void tradeCard(List<CardModel> winnerDeck, List<CardModel> looserDeck, CardModel cardToAdd) {
        if (looserDeck.isEmpty()) {
            logger.error("Looser has no cards");
            responseHandler.response("Looser has no cards");
        } else {
            for (CardModel deckCard : looserDeck) {
                if (deckCard.equals(cardToAdd)) {
                    looserDeck.remove(deckCard);
                    break;
                }
            }
            winnerDeck.add(cardToAdd);
        }
    }
}

