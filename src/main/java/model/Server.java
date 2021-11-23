package model;

import model.helper.MonsterType;
import model.helper.Type;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.log4j.Logger;

public class Server {

    private static final Logger logger = Logger.getLogger(Server.class);

    /**
     * A user buys a package
     * A packages consits of 5 Cards and costs 5 coins
     *
     * @param userModel
     */
    // TODO make it random
    public void buyPackages(UserModel userModel) {
        if (userModel.getBalance() >= 5) {
            userModel.setBalance(userModel.getBalance() - 5);

            CardModel cardModel1 = new CardModel(5, "AA", 5, Type.FIRE, MonsterType.DRAGONS, null, null);
            CardModel cardModel2 = new CardModel(6, "Money", 10, Type.NORMAL, MonsterType.FIREELVES, null, null);
            CardModel cardModel3 = new CardModel(7, "Stack", 8, Type.WATER, MonsterType.GOBLINS, null, null);
            CardModel cardModel4 = new CardModel(8, "Baum", 100, Type.FIRE, MonsterType.ORKS, null, null);

            userModel.getStackModel().addCard(cardModel1);
            userModel.getStackModel().addCard(cardModel2);
            userModel.getStackModel().addCard(cardModel3);
            userModel.getStackModel().addCard(cardModel4);

        }
    }
/*
    public static void main(String[] args) {
        UserModel firstUser = createFirstDummyUser();
        UserModel secondUser = createSecondDummyUser();

        boolean tie = true;
        for (int i = 0; i <= 100; i++) {
            CardModel cardModelFirst = getCardModel(firstUser);
            CardModel cardModelSecond = getCardModel(secondUser);
            if (cardModelFirst != null && cardModelSecond != null) {
                fight(firstUser, secondUser, cardModelFirst, cardModelSecond);
            } else {
                tie = false;
                String winner = cardModelFirst == null ? secondUser.getUsername() : firstUser.getUsername();
                logger.info("User: " + winner + " won");
                break;
            }
        }

        if (tie) {
            logger.info("Nobody won");
        }
    }  */

    public UserModel startTheGame(UserModel firstUser, UserModel secondUser) {
        for (int i = 0; i <= 100; i++) {
            CardModel cardModelFirst = getCardModel(firstUser);
            CardModel cardModelSecond = getCardModel(secondUser);
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

    private CardModel getCardModel(UserModel user) {
        try {
            Random rand = SecureRandom.getInstanceStrong();
            return user.getDeck().getCardModelList().isEmpty() ? null : user.getDeck().getCardModelList().get(rand.nextInt(user.getDeck().getCardModelList().size()));
        } catch (NoSuchAlgorithmException e) {
            logger.info("Something went wrong");
        }
        return null;
    }

    public void defineDeck(Deck deck, CardModel card) {
        if (deck.getCardModelList() != null) {
            deck.getCardModelList().add(card);
            card.setDeck(deck);
        }
    }

    public void addToStack(StackModel stackModel, CardModel card) {
        stackModel.addCard(card);
        card.setStackModel(stackModel);
    }

    private void fight(UserModel firstUser, UserModel secondUser, CardModel firstCard, CardModel secondCard) {
        int[] result = calculateDamage(firstCard, secondCard);
        if (result[2] > 0) {
            printFightingResult(firstUser, secondUser, firstCard, secondCard, result[0], result[1]);
            removeCardFromUser(firstUser, secondUser, secondCard);
        } else if (result[2] != 0) {
            removeCardFromUser(secondUser, firstUser, firstCard);
            printFightingResult(secondUser, firstUser, secondCard, firstCard, result[0], result[1]);

        }
    }

    private void printFightingResult(UserModel firstUser, UserModel secondUser, CardModel firstCard, CardModel secondCard, int firstDamage, int secondDamage) {
        logger.info("Player " + firstUser.getUsername() + ": " + firstCard.getName() + " (" + firstCard.getDamage() + ") vs "
                + "Player " + secondUser.getUsername() + ": " + secondCard.getName() + " (" + secondCard.getDamage() + ") => " +
                firstCard.getDamage() + " VS " + secondCard.getDamage() + " -> " + firstDamage + " VS " + secondDamage + " " +
                "=> " + firstCard.getName() + " defeats " + secondCard.getName());
    }

    protected void removeCardFromUser(UserModel winner, UserModel loser, CardModel card) {
        defineDeck(winner.getDeck(), card);
        loser.getDeck().getCardModelList().remove(card);
    }

    protected int[] calculateDamage(CardModel firstCard, CardModel secondCard) {
        int[] damageArray = new int[3];
        if (firstCard.getMonsterType() != null && secondCard.getMonsterType() != null) {
            damageArray[0] = validateMonsterType(firstCard, secondCard);
            damageArray[1] = validateMonsterType(secondCard, firstCard);
        } else {
            damageArray[0] = calculateNewDamage(validateType(firstCard, secondCard), firstCard);
            damageArray[1] = calculateNewDamage(validateType(secondCard, firstCard), secondCard);

        }
        damageArray[2] = damageArray[0] - damageArray[1];
        return damageArray;
    }

    protected int calculateNewDamage(int validateType, CardModel card) {
        return validateType != 0 ? card.getDamage() * validateType : card.getDamage();
    }

    protected int validateMonsterType(CardModel firstCard, CardModel secondCard) {
        MonsterType firstType = firstCard.getMonsterType();
        MonsterType secondType = secondCard.getMonsterType();
        if ((firstType == MonsterType.GOBLINS && secondType == MonsterType.DRAGONS) ||
                firstType == MonsterType.ORKS && secondType == MonsterType.WIZZARD ||
                (firstType == MonsterType.FIREELVES && secondType == MonsterType.DRAGONS)) {
            return 0;
        }
        return firstCard.getDamage();
    }

    protected int validateType(CardModel firstCard, CardModel secondCard) {
        Type firstType = firstCard.getElementType();
        Type secondType = secondCard.getElementType();

        if (firstType == Type.FIRE && secondType == Type.WATER) {
            return 2;
        } else if (firstCard.getMonsterType() == MonsterType.KNIGHTS && secondType == Type.WATER) {
            return 0;
        } else if (firstCard.getMonsterType() == MonsterType.KRAKEN) {
            return 1;
        } else if (firstType == Type.FIRE && secondType == Type.NORMAL) {
            return 2;
        } else if (firstType == Type.NORMAL && secondType == Type.WATER) {
            return 2;
        } else if (secondType == Type.FIRE && firstType == Type.WATER) {
            return (1 / 2);
        }
        return 0;
    }
}
