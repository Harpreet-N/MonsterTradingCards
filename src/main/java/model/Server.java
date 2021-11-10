package model;

import model.helper.Type;

import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

public class Server {


    /**
     * A user buys a package
     * A packages consits of 5 Cards and costs 5 coins
     *
     * @param userModel
     */
    public void buyPackages(UserModel userModel) {
        if (userModel.getBalance() >= 5) {
            userModel.setBalance(userModel.getBalance() - 5);

            // int randomNum = ThreadLocalRandom.current().nextInt(0, 100 + 1);
            CardModel cardModel1 = new CardModel(5, "AA", 10000, true, Type.FIRE, null, null);
            CardModel cardModel2 = new CardModel(6, "Money", 10000, true, Type.NORMAL, null, null);
            CardModel cardModel3 = new CardModel(7, "Stack", 10000, true, Type.WATER, null, null);
            CardModel cardModel4 = new CardModel(8, "Baum", 10000, true, Type.FIRE, null, null);

            userModel.getStackModel().addCard(cardModel1);
            userModel.getStackModel().addCard(cardModel2);
            userModel.getStackModel().addCard(cardModel3);
            userModel.getStackModel().addCard(cardModel4);

        }
    }

    public static void main(String[] args) {
        UserModel firstUser = createFirstDummyUser();
        UserModel secondUser = createSecondDummyUser();

        chooseBestCard(firstUser);
        chooseBestCard(secondUser);

        for (int i = 0; i <= 100; i++) {
            CardModel cardModelFirst = firstUser.getDeck().getCardModelList().size() > 0 ? firstUser.getDeck().getCardModelList().get(0) : null;
            CardModel cardModelSecond = secondUser.getDeck().getCardModelList().size() > 0 ? secondUser.getDeck().getCardModelList().get(0) : null;
            if (cardModelFirst != null && cardModelSecond != null) {
                fight(firstUser, secondUser, cardModelFirst, cardModelSecond);
            } else {
                System.out.println("User xy won");
                break;
            }
        }
    }

    // Card will be sorted
    protected static void chooseBestCard(UserModel firstUser) {
        firstUser.getDeck().getCardModelList().sort(Comparator.comparingInt(CardModel::getDamage).reversed());
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

    // loop over 100
    public static void fight(UserModel firstUser, UserModel secondUser, CardModel firstCard, CardModel secondCard) {
        int result = calculateDamage(firstCard, secondCard);
        if (result > 0) {
            removeCardFromUser(firstUser, secondUser, secondCard);
        } else if (result != 0) {
            removeCardFromUser(secondUser, firstUser, firstCard);
        }
    }

    protected static void removeCardFromUser(UserModel winner, UserModel loser, CardModel card) {
        defineDeck(winner.getDeck(), card);
        loser.getDeck().getCardModelList().remove(card);
    }

    public static int calculateDamage(CardModel firstCard, CardModel secondCard) {
        if (firstCard.isCardTypeMonster() && secondCard.isCardTypeMonster()) {
            return firstCard.getDamage() - secondCard.getDamage();
        } else {
            int firstDamage = validateType(firstCard, secondCard);
            int secondDamage = validateType(secondCard, firstCard);
            return firstDamage == 0 ? firstDamage : secondDamage;
        }
    }

    // TODO: Schwächere Types anpassen
    public static int validateType(CardModel firstCard, CardModel secondCard) {
        Type firstType = firstCard.getElementType();
        Type secondType = secondCard.getElementType();

        if (firstType == Type.FIRE && secondType == Type.WATER) {
            return 2;
        } else if (firstType == Type.FIRE && secondType == Type.NORMAL) {
            return 2;
        } else if (firstType == Type.NORMAL && secondType == Type.WATER) {
            return 2;
        }
        return 0;
    }


    protected static UserModel createFirstDummyUser() {
        // First user

        // Create Cards
        CardModel cardModel1 = new CardModel(1, "Monster", 20, true, Type.FIRE, null, null);
        CardModel cardModel2 = new CardModel(2, "Car", 10, true, Type.NORMAL, null, null);
        CardModel cardModel3 = new CardModel(3, "Ritter", 50, true, Type.WATER, null, null);
        CardModel cardModel4 = new CardModel(4, "Baum", 5, true, Type.FIRE, null, null);

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
        Deck deck = new Deck(userModel, null);
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
        CardModel cardModel5 = new CardModel(5, "AA", 100, true, Type.FIRE, null, null);
        CardModel cardModel6 = new CardModel(6, "Money", 100, false, Type.NORMAL, null, null);
        CardModel cardModel7 = new CardModel(7, "Stack", 50, false, Type.WATER, null, null);
        CardModel cardModel8 = new CardModel(8, "Baum", 50, true, Type.FIRE, null, null);

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
        Deck deck2 = new Deck(userModel2, null);
        defineDeck(deck2, cardModel5);
        defineDeck(deck2, cardModel6);
        defineDeck(deck2, cardModel7);
        defineDeck(deck2, cardModel8);

        // Set Deck
        userModel2.setDeck(deck2);

        return userModel2;
    }
}
