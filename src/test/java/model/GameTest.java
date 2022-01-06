package model;

import model.helper.MonsterType;
import model.helper.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import run.BattelLogic;
import service.CalculationService;
import service.DeckService;
import service.ValidateService;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
public class GameTest {

    @Mock
    private BattelLogic server;

    private UserModel playerA;

    private UserModel playerB;

    private CardModel playerCardA;

    private CardModel playerCardB;

    @Mock
    private ValidateService validateService;

    @Mock
    private CalculationService calculationService;

    @Mock
    private DeckService deckService;

    // before nur setuo usw
/*
    @BeforeEach
    @Test
    void prepareUserWithStackAndDeckTest(){
        playerCardA = new CardModel(1, "Monster", 20, Type.FIRE, null, null, null);
        playerCardB = new CardModel(2, "Car", 10, Type.NORMAL, MonsterType.KRAKEN, null, null);

        StackModel stackModelA = new StackModel();
        deckService.addToStack(stackModelA, playerCardA);

        StackModel stackModelB = new StackModel();
        deckService.addToStack(stackModelB, playerCardB);

        playerA = new UserModel("PlayA", "Secret", 20, stackModelA, null);
        playerB = new UserModel("PlayB", "Secret", 20, stackModelB, null);

        stackModelA.setUserModel(playerA);
        stackModelB.setUserModel(playerB);

        Deck deckA = new Deck(playerA);
        deckService.defineDeck(deckA, playerCardA);

        Deck deckB = new Deck(playerB);
        deckService.defineDeck(deckB, playerCardB);

        // Set Deck
        playerA.setDeck(deckA);
        playerB.setDeck(deckB);

    }

    @Test
    void fightTest(){
        assertEquals(server.startTheGame(playerA, playerB), playerA);
    }


    @ParameterizedTest(name = "Type Test {0} should return {1} {2}")
    @MethodSource("provideCardWithTypeAndExpected")
    void validateTypeTest(CardModel cardModel1, CardModel cardModel2, int expected, int expectedMonster) {
        assertThat(validateService.validateType(cardModel1.getElementType(), cardModel2.getElementType(), cardModel1.getMonsterType()))
                .isEqualTo(expected);
    }

    @ParameterizedTest(name = "Monster Type Test {0} should return {1} {2}")
    @MethodSource("provideCardWithTypeAndExpected")
    void validateMonsterTypeTest(CardModel cardModel1, CardModel cardModel2, int expected, int expectedMonster) {
        assertThat(validateService.validateMonsterType(cardModel1.getMonsterType(), cardModel2.getMonsterType(), cardModel1.getDamage()))
                .isEqualTo(expectedMonster);
    }

    public static Stream<Arguments> provideCardWithTypeAndExpected() {
        return Stream.of(
                Arguments.of(new CardModel(1, "Fire", 20, Type.FIRE, MonsterType.GOBLINS, null, null),
                        new CardModel(2, "Fire", 20, Type.FIRE, MonsterType.DRAGONS, null, null),
                        0,0),
                Arguments.of(new CardModel(3, "FIRE", 20, Type.FIRE, MonsterType.ORKS, null, null),
                        new CardModel(4, "WATER", 20, Type.WATER, MonsterType.WIZZARD, null, null),
                        3,0),
                Arguments.of(new CardModel(5, "FIRE", 20, Type.FIRE, MonsterType.FIREELVES, null, null),
                        new CardModel(6, "NORMAL", 20, Type.NORMAL, MonsterType.DRAGONS, null, null),
                        2,0),
                Arguments.of(new CardModel(7, "FIRE", 20, Type.FIRE, MonsterType.DRAGONS, null, null),
                        new CardModel(8, "WATER", 20, Type.WATER, MonsterType.FIREELVES, null, null),
                        3,0),
                Arguments.of(new CardModel(7, "NORMAL", 20, Type.NORMAL, null, null, null),
                        new CardModel(8, "WATER", 20, Type.WATER, null, null, null),
                        2,20),
                Arguments.of(new CardModel(7, "NORMAL", 20, Type.NORMAL, MonsterType.KNIGHTS, null, null),
                        new CardModel(8, "WATER", 20, Type.WATER, null, null, null),
                        0,20)
        );
    }

    @ParameterizedTest(name = "Type Test {0} should return {1} {2}")
    @MethodSource("provideCardWithDamageAndExpected")
    void validateMonsterTypeTest(int validateType, CardModel cardModel, int expected) {
        assertThat(calculationService.calculateNewDamage(validateType, cardModel))
                .isEqualTo(expected);
    }

    public static Stream<Arguments> provideCardWithDamageAndExpected() {
        return Stream.of(
                Arguments.of(0, new CardModel(1, "Fire", 20, Type.FIRE, MonsterType.GOBLINS, null, null), 20),
                Arguments.of(1, new CardModel(1, "Fire", 20, Type.FIRE, MonsterType.GOBLINS, null, null),20),
                Arguments.of(2, new CardModel(3, "FIRE", 20, Type.FIRE, MonsterType.ORKS, null, null), 40),
                Arguments.of(3, new CardModel(5, "FIRE", 20, Type.FIRE, MonsterType.FIREELVES, null, null), 10)
        );
    }

    // logik
    // when given then usw...........
    @Test
    void removeCardFromUserTest(){
        assertEquals(1, playerA.getDeck().getCardModelList().size());
        assertEquals(1, playerB.getDeck().getCardModelList().size());

        deckService.removeCardFromUser(playerA, playerB, playerCardB);
        assertEquals(2, playerA.getDeck().getCardModelList().size());
        assertEquals(0, playerB.getDeck().getCardModelList().size());
    }


/*
 protected UserModel createFirstDummyUser() {
        // First user

        // Create Cards
        CardModel cardModel1 = new CardModel(1, "Monster", 20, Type.FIRE, null, null, null);
        CardModel cardModel2 = new CardModel(2, "Car", 10, Type.NORMAL, MonsterType.KRAKEN, null, null);
        CardModel cardModel3 = new CardModel(3, "Ritter", 50, Type.WATER, MonsterType.WIZZARD, null, null);
        CardModel cardModel4 = new CardModel(4, "Baum", 5, Type.FIRE, null, null, null);

        // Create Stack and add Cards
        StackModel stackModel = new StackModel();
        addToStack(stackModel, cardModel1);
        addToStack(stackModel, cardModel2);
        addToStack(stackModel, cardModel3);
        addToStack(stackModel, cardModel4);

        // Create User
        UserModel userModel = new UserModel("Test", "Secret", 20, stackModel, null);
        stackModel.setUserModel(userModel);

        // Create Deck and add User
        Deck deck = new Deck(userModel);
        defineDeck(deck, cardModel1);
        defineDeck(deck, cardModel2);
        defineDeck(deck, cardModel3);
        defineDeck(deck, cardModel4);

        // Set Deck
        userModel.setDeck(deck);
        return userModel;
    }

    protected UserModel createSecondDummyUser() {

        // Second User

        // Create Cards
        CardModel cardModel5 = new CardModel(5, "AA", 50, Type.FIRE, null, null, null);
        CardModel cardModel6 = new CardModel(6, "Money", 1, Type.NORMAL, null, null, null);
        CardModel cardModel7 = new CardModel(7, "Stack", 5, Type.WATER, MonsterType.KNIGHTS, null, null);
        CardModel cardModel8 = new CardModel(8, "Uhr", 5, Type.FIRE, MonsterType.ORKS, null, null);

        // Create Stack and add Cards
        StackModel stackModel2 = new StackModel();
        addToStack(stackModel2, cardModel5);
        addToStack(stackModel2, cardModel6);
        addToStack(stackModel2, cardModel7);
        addToStack(stackModel2, cardModel8);

        // Create User
        UserModel userModel2 = new UserModel("NOOOIO", "Secret", 20, stackModel2, null);
        stackModel2.setUserModel(userModel2);

        // Create Deck and add User
        Deck deck2 = new Deck(userModel2);
        defineDeck(deck2, cardModel5);
        defineDeck(deck2, cardModel6);
        defineDeck(deck2, cardModel7);
        defineDeck(deck2, cardModel8);

        // Set Deck
        userModel2.setDeck(deck2);

        return userModel2;
    }
 */
}
