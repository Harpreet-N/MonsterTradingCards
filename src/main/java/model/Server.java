package model;

import model.helper.Type;

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

            CardModel cardModel1 = new CardModel(5, "AA", 100, true, Type.FIRE, null, null);
            CardModel cardModel2 = new CardModel(6, "Money", 100, false, Type.NORMAL, null, null);
            CardModel cardModel3 = new CardModel(7, "Stack", 50, false, Type.WATER, null, null);
            CardModel cardModel4 = new CardModel(8, "Baum", 50, true, Type.FIRE, null, null);


            userModel.getStackModel().addCard(cardModel1);
            userModel.getStackModel().addCard(cardModel2);
            userModel.getStackModel().addCard(cardModel3);
            userModel.getStackModel().addCard(cardModel4);

        }
    }

    public static void main(String[] args) {
        // Create Cards
        CardModel cardModel1 = new CardModel(1, "Monster", 20, true, Type.FIRE, null, null);
        CardModel cardModel2 = new CardModel(2, "Car", 10, false, Type.NORMAL, null, null);
        CardModel cardModel3 = new CardModel(3, "Ritter", 5, false, Type.WATER, null, null);
        CardModel cardModel4 = new CardModel(4, "Baum", 5, true, Type.FIRE, null, null);

        // Create Stack and add Cards
        StackModel stackModel = new StackModel();
        addToStack(stackModel,cardModel1);
        addToStack(stackModel,cardModel2);
        addToStack(stackModel,cardModel3);
        addToStack(stackModel,cardModel4);

        // Create User
        UserModel userModel = new UserModel("Test", "Secret",20, stackModel, null);
        stackModel.setUserModel(userModel);


        // Create Deck and add User
        Deck deck = new Deck(userModel, null);
        defineDeck(deck, cardModel1);
        defineDeck(deck, cardModel2);
        defineDeck(deck, cardModel3);
        defineDeck(deck, cardModel4);


        // Set Deck
        userModel.setDeck(deck);
        System.out.println("Test");


        // Get the best 4 cards?
    }

    public static void defineDeck(Deck deck, CardModel card) {
        if (deck.getCardModelList() != null && deck.getCardModelList().size() <= 4) {
            deck.getCardModelList().add(card);
            card.setDeck(deck);
        }
    }

    public static void addToStack(StackModel stackModel, CardModel card) {
        stackModel.addCard(card);
        card.setStackModel(stackModel);
    }
}
