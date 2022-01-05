package service;

import model.*;
import org.apache.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class DeckService {

    private static final Logger logger = Logger.getLogger(DeckService.class);

    public CardModel getCardModel(UserModel user) {
        try {
            Random rand = SecureRandom.getInstanceStrong();
            //return user.getDeck().getCardModelList().isEmpty() ? null : user.getDeck().getCardModelList().get(rand.nextInt(user.getDeck().getCardModelList().size()));
        } catch (NoSuchAlgorithmException e) {
            logger.info("Something went wrong");
        }
        return null;
    }

    public void defineDeck(Deck deck, CardModel card) {
        if (deck.getCardModelList() != null) {
            deck.getCardModelList().add(card);
       //     card.setDeck(deck);
        }
    }

    public void addToStack(StackModel stackModel, CardModel card) {
        stackModel.addCard(card);
   //     card.setStackModel(stackModel);
    }

    public void removeCardFromUser(UserModel winner, UserModel loser, CardModel card) {
    //    defineDeck(winner.getDeck(), card);
    //    loser.getDeck().getCardModelList().remove(card);
    }


}
