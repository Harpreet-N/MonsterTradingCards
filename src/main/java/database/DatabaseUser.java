package database;

import model.CardModel;
import model.Monster;
import model.Spell;
import model.UserModel;
import model.helper.MonsterType;
import model.helper.Type;
import org.apache.log4j.Logger;
import repository.UserDtoRepository;
import service.AuthenticationService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUser implements UserDtoRepository {

    private static final Logger logger = Logger.getLogger(DatabaseUser.class);

    private final Statement stmt;
    private final Connection connection;

    public DatabaseUser(Statement stmt, Connection connection) {
        this.stmt = stmt;
        this.connection = connection;
    }

    @Override
    public boolean createUser(UserModel userModel) {
        try (PreparedStatement registerUserStmt = this.connection.prepareStatement("INSERT INTO users (username, password) VALUES(?, ?)");) {
            registerUserStmt.setString(1, userModel.getUsername());
            registerUserStmt.setString(2, AuthenticationService.hashPassword(userModel.getPassword()));
            registerUserStmt.executeUpdate();
            logger.info("UserModel was created");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return false;
    }


    @Override
    public boolean editUser(UserModel userModel) {
        try (PreparedStatement setUserData = this.connection.prepareStatement
                ("UPDATE users SET name=?, bio=?, image=?, balance=?, elo=?, wins=?, looses=? WHERE username=? ;");) {
            setUserData.setString(1, userModel.getName());
            setUserData.setString(2, userModel.getBio());
            setUserData.setString(3, userModel.getImage());
            setUserData.setInt(4, userModel.getBalance());
            setUserData.setInt(5, userModel.getElo());
            setUserData.setInt(6, userModel.getWins());
            setUserData.setInt(7, userModel.getLooses());
            setUserData.setString(8, userModel.getUsername());
            setUserData.executeUpdate();

        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }

        logger.info(userModel.getUsername() + " has changed his user data!");
        return true;
    }

    @Override
    public boolean compareExchangeToken(String username, String token) {
        try {
            if (userExists(username)) {
                ResultSet rs = stmt.executeQuery(DatabaseQuery.SELECT_TOKEN.getQuery() + username + "'");

                if (rs.next()) {
                    if (rs.getString("token").equals(token)) {
                        return true;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    @Override
    public UserModel loginUser(String username, String password) {
        String exchangeToken = "mtcgToken";
        try {
            if (this.userExists(username)) {
                //PreparedStatement --> sql inj
                ResultSet rs = stmt.executeQuery(DatabaseQuery.SELECT_BY_USERNAME.getQuery() + username + "'");

                while (rs.next()) {
                    if (AuthenticationService.passwordIsEqual(password, rs.getString("password"))) {

                        try (PreparedStatement setExchangeToken = this.connection.prepareStatement("UPDATE users SET token= ? WHERE username = ? ;");) {
                            setExchangeToken.setString(1, exchangeToken);
                            setExchangeToken.setString(2, username);
                            setExchangeToken.executeUpdate();

                            logger.info("UserModel logged in with token: " + exchangeToken);
                            return new UserModel(
                                    rs.getString("username"),
                                    rs.getString("name"),
                                    rs.getString("password"),
                                    exchangeToken,
                                    rs.getString("bio"),
                                    rs.getString("image"),
                                    rs.getInt("balance"),
                                    new ArrayList<>(),
                                    new ArrayList<>(),
                                    rs.getInt("elo"),
                                    rs.getInt("wins"),
                                    rs.getInt("looses")
                            );
                        } catch (SQLException e) {
                            logger.error(e.getMessage());
                        }
                    } else {
                        logger.error("Password for user " + username + " is incorrect.");
                    }
                }
                return null;
            } else {
                logger.error("UserModel does not exist.");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return null;
    }


    public void addWin(String username) {
        if (userExists(username)) {
            try (PreparedStatement setWinStatistics = this.connection.prepareStatement("UPDATE users SET wins=wins+1, elo=elo+3 WHERE username =?;");) {
                setWinStatistics.setString(1, username);
                setWinStatistics.executeUpdate();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.error("Error: UserModel does not exist!");
        }
    }

    public void addLoss(String username) {
        if (userExists(username)) {
            try (PreparedStatement setLooseStatistics = this.connection.prepareStatement("UPDATE users SET looses=looses+1, elo=elo-5 WHERE username =?;");) {
                setLooseStatistics.setString(1, username);
                setLooseStatistics.executeUpdate();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.error("Error: UserModel does not exist!");
        }
    }



    @Override
    public void addResultSetToArray(List<CardModel> listOfCards, ResultSet rs) throws SQLException {
        if (rs.next()) {
            do {
                if (rs.getString("cardtype").equalsIgnoreCase(MonsterType.SPELL.name())) {
                    // CardModel is spell
                    listOfCards.add(new Spell(rs.getString("uuid"), rs.getString("owner"), null,
                            Type.valueOf(rs.getString("elementtype").toUpperCase()),
                            MonsterType.valueOf(rs.getString("cardtype").toUpperCase()),
                            rs.getInt("damage")
                    ));
                } else {
                    // CardModel is monster
                    listOfCards.add(new Monster(rs.getString("uuid"), rs.getString("owner"), null,
                            Type.valueOf(rs.getString("elementtype").toUpperCase()),
                            MonsterType.valueOf(rs.getString("cardtype").toUpperCase()),
                            rs.getInt("damage")
                    ));
                }
            } while (rs.next());
        }
    }

    @Override
    public boolean checkIfCardIsLocked(String uuid) {
        try {
            ResultSet rs = this.stmt.executeQuery(DatabaseQuery.SELECT_LOCKED_CARD.getQuery() + uuid + "'");
            if (!rs.next()) {
                logger.error("CardModel not found");
            } else {
                if (rs.getBoolean("locked")) {
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    // hier stimmt was nicht
    @Override
    public List<CardModel> getDeck(String username) {
        List<CardModel> userDeck = new ArrayList<>();
        try {
            if (userExists(username)) {
                ResultSet rs = this.stmt.executeQuery(DatabaseQuery.SELECT_FETCH_STACK_BY_USER.getQuery() + username + "'");
                addResultSetToArray(userDeck, rs);
            } else {
                logger.error("UserModel does not exist!");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return userDeck;
    }

    public int getDeckSize(String username) {
        List<CardModel> deck = getDeck(username);
        return deck.size();
    }

    @Override
    public UserModel getUserData(String username) {
        UserModel userModel = null;
        try {
            if (userExists(username)) {
                ResultSet rs = this.stmt.executeQuery(DatabaseQuery.SELECT_BY_USERNAME.getQuery() + username + "'");
                if (rs.next()) {
                    logger.info(username + " has requested his user data!");
                    return new UserModel(
                            rs.getString("username"),
                            rs.getString("name"),
                            rs.getString("password"),
                            rs.getString("token"),
                            rs.getString("bio"),
                            rs.getString("image"),
                            rs.getInt("balance"),
                            new ArrayList<>(),
                            new ArrayList<>(),
                            rs.getInt("elo"),
                            rs.getInt("wins"),
                            rs.getInt("looses")
                    );
                } else {
                    logger.error("No ResultSet found");
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return userModel;
    }

    @Override
    public boolean configureDeck(String username, List<String> deck) {
        if (deck.size() != 4) {
            logger.error("Deck has too many or too less cards!");
            return false;
        }
        if (userExists(username)) {
            for (String uuid : deck) {
                if (!checkIfCardIsLocked(uuid)) {
                    try(PreparedStatement setPackageBuyer = this.connection.prepareStatement("UPDATE cards SET storagetype='deck' WHERE uuid=? AND owner=? ;");) {
                        setPackageBuyer.setString(1, uuid);
                        setPackageBuyer.setString(2, username);
                        setPackageBuyer.executeUpdate();
                    } catch (SQLException e) {
                        logger.error(e.getMessage());
                    }
                } else {
                    logger.error("Cannot add card - is locked");
                    return false;
                }
            }
            logger.info(username + " configured a deck!");
            return true;
        } else {
            logger.error("UserModel does not exist!");
        }
        return false;
    }

    @Override
    public List<CardModel> getStack(String username) {
        List<CardModel> userStack = new ArrayList<>();
        try {
            if (userExists(username)) {
                ResultSet rs = this.stmt.executeQuery(DatabaseQuery.SELECT_FETCH_STACK_BY_USER.getQuery() + username + "'");

                addResultSetToArray(userStack, rs);
            } else {
                logger.error("Error: UserModel does not exist!");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return userStack;
    }

    @Override
    public boolean addPackage(List<CardModel> packageToAdd) {
        String packageId = AuthenticationService.generateAuthToken();

        for (CardModel c : packageToAdd) {
            try (PreparedStatement insertPackageIntoDB = this.connection.prepareStatement("INSERT INTO cards (uuid, packageId, cardtype, elementtype, damage, storagetype) VALUES(?, ?, ?, ?, ?, ?)");){
                insertPackageIntoDB.setString(1, c.getId());
                insertPackageIntoDB.setString(2, packageId);
                insertPackageIntoDB.setString(3, c.getMonsterType().name());
                insertPackageIntoDB.setString(4, c.getElementType().name());
                insertPackageIntoDB.setDouble(5, c.getDamage());
                insertPackageIntoDB.setString(6, "package");
                insertPackageIntoDB.executeUpdate();
                logger.info("Added package " + packageId + " to DB");
            }  catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
        return false;
    }

    @Override
    public List<CardModel> getAllCards(String username) {
        List<CardModel> allCards = new ArrayList<>(getStack(username));
        allCards.addAll(getDeck(username));

        return allCards;
    }

    public StringBuilder retrieveScoreboard() {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n---- ScoreBoard ----\r\n");
        int i = 1;

        try {
            ResultSet rs = this.stmt.executeQuery(DatabaseQuery.SELECT_ORDER_USERS_BY_ELO.getQuery());

            if (rs.next()) {
                do {
                    sb.append(i)
                            .append(". Platz: ")
                            .append(rs.getString("username"))
                            .append(" mit ")
                            .append(+rs.getInt("elo"))
                            .append(" Elo (")
                            .append(rs.getInt("wins"))
                            .append(" Wins / ")
                            .append(rs.getInt("looses"))
                            .append(" Looses) \r\n");
                    i++;
                } while (rs.next());
            } else {
                logger.error("ResultSet is empty");
                return sb;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        sb.append("\r\n----------------------\r\n");

        return sb;
    }




    @Override
    public boolean userExists(String username) {
        try {
            ResultSet rs = this.stmt.executeQuery(DatabaseQuery.SELECT_USERNAME.getQuery());

            if (rs.next()) {
                do {
                    if (rs.getString("username").equals(username)) {
                        return true;
                    }
                } while (rs.next());
            } else {
                return false;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return false;
    }
}
