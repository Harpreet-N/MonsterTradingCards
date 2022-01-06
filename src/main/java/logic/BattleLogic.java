package logic;

import database.Database;
import database.DatabaseUser;
import http.ResponseHandler;
import model.CardModel;
import model.Spell;
import model.battle.Battle;
import model.helper.MonsterType;
import model.helper.Type;
import org.apache.log4j.Logger;
import service.MonsterService;
import service.RandomService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BattleLogic {
    
    private static final Logger logger = Logger.getLogger(BattleLogic.class);
    private final Database dbA;
    private DatabaseUser db;
    private final ConcurrentLinkedQueue<String> battleQueue = new ConcurrentLinkedQueue<>();

    private MonsterService monsterService;


    public BattleLogic(Database dbA) {
        this.dbA = dbA;
        db = new DatabaseUser(dbA.getStmt(), dbA.getConnection());
    }

    public synchronized void queueFighter(ResponseHandler rph, String username) throws IOException {
        battleQueue.add(username);

        if ((battleQueue.size() % 2 == 0) && (battleQueue.size() != 0)) {
            String usernameOne = battleQueue.poll();
            String usernameTwo = battleQueue.poll();

            logger.info("Battle will be started: " + usernameOne + " vs " + usernameTwo);
            rph.response("Battle will be started: " + usernameOne + " vs " + usernameTwo);

            startBattle(rph, usernameOne, usernameTwo);

            return;
        }

        logger.info(username +  " added to battle queue!");
        rph.response(username + " added to battle queue!");
    }

    private void startBattle(ResponseHandler rph, String usernameOne, String usernameTwo) {
        int MAX_ROUNDS = 100;

        if (db.getDeckSize(usernameOne) != 4 && db.getDeckSize(usernameTwo) != 4) {
            logger.error("Deck has more or less than 4 cards.");
            return;
        }

        List<CardModel> copyOfDeckOne = new ArrayList<>(db.getDeck(usernameOne));
        List<CardModel> copyOfDeckTwo = new ArrayList<>(db.getDeck(usernameTwo));

        for (int i = 0; i < MAX_ROUNDS; i++) {
            if (copyOfDeckTwo.isEmpty()) {
                logger.info(usernameOne + " won!");
                return;
            } else if (copyOfDeckOne.isEmpty()) {
                logger.info(usernameTwo + " won!");
                return;
            } else {
                System.out.print((i + 1) + ": ");

                CardModel winnerCard = null;
                CardModel looserCard = null;

                CardModel randomCardOfUserOne = RandomService.pickRandomCardFromDeck(copyOfDeckOne);
                CardModel randomCardOfUserTwo = RandomService.pickRandomCardFromDeck(copyOfDeckTwo);

                String cardNameOne = randomCardOfUserOne.getMonsterType().name();
                String cardNameTwo = randomCardOfUserTwo.getMonsterType().name();

                String cardElementTypeOne = randomCardOfUserOne.getElementType().name();
                String cardElementTypeTwo = randomCardOfUserTwo.getElementType().name();

                if (!cardNameOne.equals(MonsterType.SPELL.name()) && !cardNameTwo.equals(MonsterType.SPELL.name())) {
                    Battle battle = null;
                    battle = monsterService.getWinnerOfMonsterBattle(randomCardOfUserOne, randomCardOfUserTwo);

                    String battleEndMessage = usernameOne + ": " + cardElementTypeOne + cardNameOne
                            + " (" + randomCardOfUserOne.getDamage() + " Damage) vs " + usernameTwo + ": " + cardElementTypeTwo + cardNameTwo
                            + " (" + randomCardOfUserTwo.getDamage() + " Damage) => ";

                    if (battle != null) {
                        winnerCard = battle.getWinner();
                        looserCard = battle.getLooser();

                        if (battle.getWinner().equals(randomCardOfUserOne)) {
                            db.addWin(usernameOne);
                            db.addLoss(usernameTwo);

                            this.tradeCard(copyOfDeckOne, copyOfDeckTwo, randomCardOfUserTwo);
                        } else {
                            db.addWin(usernameTwo);
                            db.addLoss(usernameOne);

                            this.tradeCard(copyOfDeckTwo, copyOfDeckOne, randomCardOfUserOne);
                        }

                        logger.info(battleEndMessage + winnerCard.getElementType().name() + winnerCard.getMonsterType().name() + " defeats " + looserCard.getElementType().name() + looserCard.getMonsterType().name());
                    } else {
                        logger.info(battleEndMessage + "Draw");
                    }
                } else {
                    double damageOne = randomCardOfUserOne.getDamage();
                    double damageTwo = randomCardOfUserTwo.getDamage();

                    String battleEndMessage = usernameOne + ": " + cardElementTypeOne + cardNameOne + " (" + randomCardOfUserOne.getDamage() + " Damage) vs " +
                            usernameTwo + ": " + cardElementTypeTwo + cardNameTwo + " (" + randomCardOfUserTwo.getDamage() + " Damage) => " + randomCardOfUserOne.getDamage() + " vs " + randomCardOfUserTwo.getDamage() +
                            " -> " + damageOne + " vs " + damageTwo + " => ";

                    if (cardNameOne.equals(MonsterType.KNIGHT.name()) && (cardNameTwo.equals(MonsterType.SPELL.name()) && cardElementTypeTwo.equals(Type.WATER.name()))) {
                        // Knight vs WaterSpell
                        db.addWin(usernameTwo);
                        db.addLoss(usernameOne);

                        logger.info(battleEndMessage + randomCardOfUserTwo.getElementType().name() + randomCardOfUserTwo.getMonsterType().name() + " wins");

                        this.tradeCard(copyOfDeckTwo, copyOfDeckOne, randomCardOfUserOne);
                    } else if ((cardNameOne.equals(MonsterType.SPELL.name()) && cardElementTypeOne.equals(Type.WATER.name())) && cardNameOne.equals(MonsterType.KNIGHT.name())) {
                        // WaterSpell vs Knight
                        db.addWin(usernameOne);
                        db.addLoss(usernameTwo);

                        logger.info(battleEndMessage + randomCardOfUserOne.getElementType().name() + randomCardOfUserOne.getMonsterType().name() + " wins");

                        this.tradeCard(copyOfDeckOne, copyOfDeckTwo, randomCardOfUserTwo);
                    } else {
                        if (Spell.elementTypeIsEffective(randomCardOfUserOne.getElementType(), randomCardOfUserTwo.getElementType())) {
                            // Fire vs Water
                            damageOne = damageOne * 2;
                            damageTwo = damageTwo / 2;
                        } else if (Spell.elementTypeIsEffective(randomCardOfUserTwo.getElementType(), randomCardOfUserOne.getElementType())) {
                            damageOne = damageOne / 2;
                            damageTwo = damageTwo * 2;
                        }

                        battleEndMessage = usernameOne + ": " + cardElementTypeOne + cardNameOne + " (" + randomCardOfUserOne.getDamage() + " Damage) vs " +
                                usernameTwo + ": " + cardElementTypeTwo + cardNameTwo + " (" + randomCardOfUserTwo.getDamage() + " Damage) => " + randomCardOfUserOne.getDamage() + " vs " + randomCardOfUserTwo.getDamage() +
                                " -> " + damageOne + " vs " + damageTwo + " => ";

                        if (damageOne > damageTwo) {
                            // One is winnerCard
                            db.addWin(usernameOne);
                            db.addLoss(usernameTwo);

                            logger.info(battleEndMessage + cardElementTypeOne + cardNameOne + " wins");

                            this.tradeCard(copyOfDeckOne, copyOfDeckTwo, randomCardOfUserTwo);
                        } else if (damageTwo > damageOne) {
                            // Two is winnerCard
                            db.addWin(usernameTwo);
                            db.addLoss(usernameOne);

                            logger.info(battleEndMessage + cardElementTypeTwo + cardNameTwo + " wins");

                            this.tradeCard(copyOfDeckTwo, copyOfDeckOne, randomCardOfUserOne);
                        } else {
                            // Draw
                            logger.info(battleEndMessage + "Draw");
                        }
                    }
                }
            }
        }

        logger.info("Battle was a draw!");
    }

    private void tradeCard(List<CardModel> winnerDeck, List<CardModel> looserDeck, CardModel cardToAdd) {
        if (looserDeck.isEmpty()) {
            logger.error("Looser has no cards");
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

