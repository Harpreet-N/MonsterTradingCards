package model;

import model.helper.Type;

public class Server {

    public void buyPackages(UserModel userModel){
        if(userModel.getBalance() >= 5) {
            userModel.setBalance(userModel.getBalance() - 5);


            // Add Cards
            userModel.getStackModel().getCardModelList().add(new CardModel(1,"AA", 10, true, Type.FIRE));
        }
    }


    public void defineDeck(UserModel userModel) {
        // Get the best 4 cards?
    }
}
