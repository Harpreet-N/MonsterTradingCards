package model;

import model.helper.MonsterType;
import model.helper.Type;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CardModelTest {


    @Test
    public void test() {
        assertEquals(10,10);
    }


    @Test
    public void createModel() {
        CardModel cardModel = new CardModel(5, "AA", 100, Type.FIRE, null, null, null);

        assertEquals("AA", cardModel.getName());
        assertEquals(100, cardModel.getDamage());
        assertEquals(Type.FIRE, cardModel.getElementType());

    }
}