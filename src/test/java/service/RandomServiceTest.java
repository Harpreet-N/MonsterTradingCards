package service;

import model.CardModel;
import model.Monster;
import model.Spell;
import model.helper.MonsterType;
import model.helper.Type;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RandomServiceTest {

    private RandomService randomService = new RandomService();

    @Test
    void getRandomTypeTest() {
        assertNotNull(randomService.getRandomType());
    }

    @Test
    void getRandomMonsterTypeTest() {
        assertNotNull(randomService.getRandomMonsterType());
    }

    @Test
    void getRandomCardFromDeckTest() {
        CardModel monster = new Monster("MonsterI", "Test","PAcka", Type.FIRE, MonsterType.WIZZARD, 420);
        CardModel monster1 = new Monster("MonsterID", "Test","PAcka", Type.FIRE, MonsterType.WIZZARD, 420);

        CardModel spell = new Spell("SpellI", "Test","PAcka", Type.FIRE, MonsterType.WIZZARD, 420);
        CardModel spell1 = new Spell("SpellId", "Test","PAcka", Type.FIRE, MonsterType.WIZZARD, 420);

        List<CardModel> cardModelList = new ArrayList<>();
        cardModelList.add(monster);
        cardModelList.add(monster1);
        cardModelList.add(spell);
        cardModelList.add(spell1);

        assertNotNull(randomService.getRandomCardFromDeck(cardModelList));
    }

    @Test
    void getRandomCardFromDeckWithOneMonsterTest() {
        CardModel monster = new Monster("MonsterI", "Test","PAcka", Type.FIRE, MonsterType.WIZZARD, 420);

        List<CardModel> cardModelList = new ArrayList<>();
        cardModelList.add(monster);

        assertEquals(randomService.getRandomCardFromDeck(cardModelList), monster);
    }

}
