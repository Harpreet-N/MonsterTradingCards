package service;

import model.CardModel;
import org.apache.log4j.Logger;

import java.util.List;

public class DeckService {

    private static final Logger logger = Logger.getLogger(DeckService.class);

    public void removeCardFromUser(List<CardModel> deckOne, List<CardModel> deckTwo, CardModel card) {
        if (deckTwo.isEmpty()) {
            logger.error("Loser has no card left in deck");
        } else {
            for (CardModel deckCard : deckTwo) {
                if (deckCard.equals(card)) {
                    deckTwo.remove(card);
                    deckOne.add(card);
                    break;
                }
            }
        }
    }

}
