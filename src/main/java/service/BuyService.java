package service;

import model.CardModel;
import model.UserModel;
import model.helper.MonsterType;
import model.helper.Type;

public class BuyService {

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
}
