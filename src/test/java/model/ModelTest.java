package model;

import model.helper.Type;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {

    private UserModel userModel;

    private StackModel stackModel;

    private CardModel cardModel;

    private Deck deck;

    @Mock
    private Server server = new Server();


    @Test
    @Before
    public void createCardTest() {
        cardModel = new CardModel(1, "Monster", 20, Type.FIRE, null, null, null);
        assertEquals("Monster", cardModel.getName());
        assertEquals(20, cardModel.getDamage());
    }

    @Test
    @Before
    public void createUserTest() {
        userModel = new UserModel("Test", "Secret", 20, null, null);
        stackModel = new StackModel();
        deck = new Deck(userModel);

        stackModel.setUserModel(userModel);
        userModel.setStackModel(stackModel);
        this.server.defineDeck(deck, cardModel);

        assertEquals("Test", userModel.getUsername());
        assertEquals("Secret", userModel.getPassword());
        assertEquals(20, userModel.getBalance());
    }


    @Test
    public void addStackEmptyToUserTest() {
        assertEquals(stackModel, userModel.getStackModel());
    }

    @Test
    public void addStackToUserTest() {
        stackModel.addCard(cardModel);
        assertEquals(stackModel.getCardModelList(), userModel.getStackModel().getCardModelList());
    }

    @Test
    public void addDeckToUserTest() {
        this.server.defineDeck(deck, cardModel);
        userModel.setDeck(deck);
        assertEquals(deck.getUserModel(), userModel);
    }


    @Test
    public void buyPackagesTest(){
        assertEquals(20,userModel.getBalance());
        assertEquals(0,userModel.getStackModel().getCardModelList().size());

        this.server.buyPackages(userModel);

        assertEquals(15,userModel.getBalance());
        assertEquals(4,userModel.getStackModel().getCardModelList().size());

    }

}