package util;

import model.CardModel;
import model.helper.MonsterType;

import java.util.List;
import java.util.Random;

public class CardUtil {

    public static boolean cardIsSpell(CardModel c) {
        return c.getMonsterType() == MonsterType.SPELL;
    }

    public static CardModel pickRandomCardFromDeck(List<CardModel> deck) {
        Random rand = new Random();

        return deck.get(rand.nextInt(deck.size()));
    }
}
