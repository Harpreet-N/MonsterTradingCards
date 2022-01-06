package database;

import model.CardModel;
import model.Monster;
import model.Spell;
import model.UserModel;
import model.helper.MonsterType;
import model.helper.Type;
import org.apache.log4j.Logger;
import service.AuthenticationService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUser {

    private static final Logger logger = Logger.getLogger(DatabaseUser.class);

    private final Statement stmt;
    private final Connection connection;

    public DatabaseUser(Statement stmt, Connection connection) {
        this.stmt = stmt;
        this.connection = connection;
    }

    public boolean createUser(String username, String password) {
        if (!this.userExists(username)) {
            try (PreparedStatement registerUserStmt = this.connection.prepareStatement("INSERT INTO users (username, password) VALUES(?, ?)");) {
                registerUserStmt.setString(1, username);
                registerUserStmt.setString(2, AuthenticationService.hashPassword(password));
                registerUserStmt.executeUpdate();

                logger.info("UserModel was created");
                return true;
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.error("Username exists.");
        }
        return false;
    }

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

    public UserModel loginUser(String username, String password) {
        String exchangeToken = "mtcgToken";
        try {
            if (this.userExists(username)) {
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

    private void addResultSetToArray(List<CardModel> listOfCards, ResultSet rs) throws SQLException {
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

    private boolean checkIfCardIsLocked(String uuid) {
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

    public List<CardModel> getDeck(String username) {
        List<CardModel> userDeck = new ArrayList<>();
        try {
            if (userExists(username)) {
                ResultSet rs = this.stmt.executeQuery(DatabaseQuery.SELECT_FETCH_DECK_BY_USER.getQuery() + username + "'");
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
                return true;
            }  catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
        return false;
    }

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

    public boolean editUserData(String userToEdit, String name, String bio, String image) {
        try {
            if (this.userExists(userToEdit)) {
                ResultSet rs = stmt.executeQuery(DatabaseQuery.SELECT_BY_USERNAME.getQuery() + userToEdit + "'");

                if (rs.next()) {
                    // Insert updated name, bio and image
                    try (PreparedStatement setUserData = this.connection.prepareStatement("UPDATE users SET name=?, bio=?, image=? WHERE username=? ;");) {
                        setUserData.setString(1, name);
                        setUserData.setString(2, bio);
                        setUserData.setString(3, image);
                        setUserData.setString(4, userToEdit);
                        setUserData.executeUpdate();
                    } catch (SQLException e) {
                        logger.error(e.getMessage());
                    }

                    logger.info(userToEdit + " has changed his user data!");
                } else {
                    logger.error("ResultSet is empty!");
                }
            } else {
                logger.error("UserModel does not exist.");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

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
