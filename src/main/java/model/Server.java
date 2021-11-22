package model;

import model.helper.MonsterType;
import model.helper.Type;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.log4j.Logger;


public class Server {


    /**
     * A user buys a package
     * A packages consits of 5 Cards and costs 5 coins
     *
     * @param userModel
     */

    private static final Logger logger = Logger.getLogger(Server.class);

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
    }

    private static CardModel getCardModel(UserModel user) {
        try {
            Random rand = SecureRandom.getInstanceStrong();
            return user.getDeck().getCardModelList().isEmpty() ? null : user.getDeck().getCardModelList().get(rand.nextInt(user.getDeck().getCardModelList().size()));
        } catch (NoSuchAlgorithmException e) {
            logger.info("Something went wrong");
        }
        return null;
    }

    public static void defineDeck(Deck deck, CardModel card) {
        if (deck.getCardModelList() != null) {
            deck.getCardModelList().add(card);
            card.setDeck(deck);
        }
    }

    public static void addToStack(StackModel stackModel, CardModel card) {
        stackModel.addCard(card);
        card.setStackModel(stackModel);
    }

    public static void fight(UserModel firstUser, UserModel secondUser, CardModel firstCard, CardModel secondCard) {
        int[] result = calculateDamage(firstCard, secondCard);
        if (result[2] > 0) {
            printFightingResult(firstUser, secondUser, firstCard, secondCard, result[0], result[1]);
            removeCardFromUser(firstUser, secondUser, secondCard);
        } else if (result[2] != 0) {
            removeCardFromUser(secondUser, firstUser, firstCard);
            printFightingResult(secondUser, firstUser, secondCard, firstCard, result[0], result[1]);

        }
    }

    private static void printFightingResult(UserModel firstUser, UserModel secondUser, CardModel firstCard, CardModel secondCard, int firstDamage, int secondDamage) {
        logger.info("Player " + firstUser.getUsername() + ": " + firstCard.getName() + " (" + firstCard.getDamage() + ") vs "
                + "Player " + secondUser.getUsername() + ": " + secondCard.getName() + " (" + secondCard.getDamage() + ") => " +
                firstCard.getDamage() + " VS " + secondCard.getDamage() + " -> " + firstDamage + " VS " + secondDamage + " " +
                "=> " + firstCard.getName() + " defeats " + secondCard.getName());
    }

    protected static void removeCardFromUser(UserModel winner, UserModel loser, CardModel card) {
        defineDeck(winner.getDeck(), card);
        loser.getDeck().getCardModelList().remove(card);
    }

    public static int[] calculateDamage(CardModel firstCard, CardModel secondCard) {
        int[] damageArray = new int[3];
        if (firstCard.getMonsterType() != null && secondCard.getMonsterType() != null) {
            damageArray[0] = validateMonsterType(firstCard, secondCard);
            damageArray[1] = validateMonsterType(secondCard, firstCard);
            damageArray[2] = damageArray[0] - damageArray[1];
        } else {
            damageArray[0] = calculateNewDamage(validateType(firstCard, secondCard), firstCard);
            damageArray[1] = calculateNewDamage(validateType(secondCard, firstCard), secondCard);
            damageArray[2] = damageArray[0] - damageArray[1];

        }
        return damageArray;
    }

    private static int calculateNewDamage(int validateType, CardModel card) {
        return validateType != 0 ? card.getDamage() * validateType : card.getDamage();
    }

    protected static int validateMonsterType(CardModel firstCard, CardModel secondCard) {
        MonsterType firstType = firstCard.getMonsterType();
        MonsterType secondType = secondCard.getMonsterType();
        if ((firstType == MonsterType.GOBLINS && secondType == MonsterType.DRAGONS) ||
                firstType == MonsterType.ORKS && secondType == MonsterType.WIZZARD ||
                (firstType == MonsterType.FIREELVES && secondType == MonsterType.DRAGONS)) {
            return 0;
        }
        return firstCard.getDamage();
    }

    public static int validateType(CardModel firstCard, CardModel secondCard) {
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


    protected static UserModel createFirstDummyUser() {
        // First user

        // Create Cards
        CardModel cardModel1 = new CardModel(1, "Monster", 20, Type.FIRE, null, null, null);
        CardModel cardModel2 = new CardModel(2, "Car", 10, Type.NORMAL, MonsterType.KRAKEN, null, null);
        CardModel cardModel3 = new CardModel(3, "Ritter", 50, Type.WATER, MonsterType.WIZZARD, null, null);
        CardModel cardModel4 = new CardModel(4, "Baum", 5, Type.FIRE, null, null, null);

        // Create Stack and add Cards
        StackModel stackModel = new StackModel();
        addToStack(stackModel, cardModel1);
        addToStack(stackModel, cardModel2);
        addToStack(stackModel, cardModel3);
        addToStack(stackModel, cardModel4);

        // Create User
        UserModel userModel = new UserModel("Test", "Secret", 20, stackModel, null);
        stackModel.setUserModel(userModel);

        // Create Deck and add User
        Deck deck = new Deck(userModel);
        defineDeck(deck, cardModel1);
        defineDeck(deck, cardModel2);
        defineDeck(deck, cardModel3);
        defineDeck(deck, cardModel4);

        // Set Deck
        userModel.setDeck(deck);
        return userModel;
    }

    protected static UserModel createSecondDummyUser() {

        // Second User

        // Create Cards
        CardModel cardModel5 = new CardModel(5, "AA", 50, Type.FIRE, null, null, null);
        CardModel cardModel6 = new CardModel(6, "Money", 1, Type.NORMAL, null, null, null);
        CardModel cardModel7 = new CardModel(7, "Stack", 5, Type.WATER, MonsterType.KNIGHTS, null, null);
        CardModel cardModel8 = new CardModel(8, "Uhr", 5, Type.FIRE, MonsterType.ORKS, null, null);

        // Create Stack and add Cards
        StackModel stackModel2 = new StackModel();
        addToStack(stackModel2, cardModel5);
        addToStack(stackModel2, cardModel6);
        addToStack(stackModel2, cardModel7);
        addToStack(stackModel2, cardModel8);

        // Create User
        UserModel userModel2 = new UserModel("NOOOIO", "Secret", 20, stackModel2, null);
        stackModel2.setUserModel(userModel2);

        // Create Deck and add User
        Deck deck2 = new Deck(userModel2);
        defineDeck(deck2, cardModel5);
        defineDeck(deck2, cardModel6);
        defineDeck(deck2, cardModel7);
        defineDeck(deck2, cardModel8);

        // Set Deck
        userModel2.setDeck(deck2);

        return userModel2;
    }
}
