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

public class DeckServiceTest {


    private DeckService deckService = new DeckService();

    @Test
    void getRandomCardFromDeckTest() {
        CardModel monster = new Monster("MonsterI", "Test", "PAcka", Type.FIRE, MonsterType.WIZZARD, 420);
        CardModel monster1 = new Monster("MonsterID", "Test", "PAcka", Type.FIRE, MonsterType.WIZZARD, 420);

        CardModel spell = new Spell("SpellI", "Test", "PAcka", Type.FIRE, MonsterType.WIZZARD, 420);
        CardModel spell1 = new Spell("SpellId", "Test", "PAcka", Type.FIRE, MonsterType.WIZZARD, 420);

        List<CardModel> cardModelList = new ArrayList<>();
        cardModelList.add(monster);
        cardModelList.add(spell);

        List<CardModel> cardModelList1 = new ArrayList<>();
        cardModelList1.add(monster1);
        cardModelList1.add(spell1);

        assertEquals(cardModelList.size(), cardModelList1.size());
        deckService.removeCardFromUser(cardModelList, cardModelList1, monster1);

        assertEquals(cardModelList.size(), 3);
    }
}
