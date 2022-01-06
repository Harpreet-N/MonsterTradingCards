package service;
import model.CardModel;
import model.helper.MonsterType;
import org.apache.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class RandomService {

    private static final Logger logger = Logger.getLogger(RandomService.class);

    public static boolean cardIsSpell(CardModel c) {
        return c.getMonsterType() == MonsterType.SPELL;
    }

    public static CardModel pickRandomCardFromDeck(List<CardModel> deck) {
        try {
            Random rand = SecureRandom.getInstanceStrong();
            return deck.get(rand.nextInt(deck.size()));
        } catch (NoSuchAlgorithmException e) {
            logger.info("Something went wrong");
        }
        return null;
    }
}
