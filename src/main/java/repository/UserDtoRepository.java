package repository;

import model.CardModel;
import model.UserModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface UserDtoRepository {
    boolean createUser(UserModel userModel);

    boolean editUser(UserModel userModel);

    UserModel getUserData(String username);



    // im Service Class
    boolean userExists(String username);

    UserModel loginUser(String username, String password);

    boolean compareExchangeToken(String username, String token);

    void addResultSetToArray(List<CardModel> listOfCards, ResultSet rs) throws SQLException;

    boolean checkIfCardIsLocked(String uuid);

    List<CardModel> getDeck(String username);


    boolean configureDeck(String username, List<String> deck);

    List<CardModel> getStack(String username);

    boolean addPackage(List<CardModel> packageToAdd);

    List<CardModel> getAllCards(String username);
}
