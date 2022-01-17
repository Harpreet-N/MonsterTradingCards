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
    public boolean isTokenEqual(String username, String token) {
        try (PreparedStatement ps = this.connection.prepareStatement("SELECT token FROM users WHERE username= ? ;");) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getString("token").equals(token)) {
                    return true;
                }
            } else {
                return false;
            }
            rs.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return false;
    }


    @Override
    public UserModel loginUser(String username, String password) {
        String exchangeToken = "mtcgToken";
        UserModel userModel = getUserData(username);
        if (AuthenticationService.passwordIsEqual(password, userModel.getPassword())) {
            try (PreparedStatement setExchangeToken = this.connection.prepareStatement("UPDATE users SET token= ? WHERE username = ? ;");) {
                setExchangeToken.setString(1, exchangeToken);
                setExchangeToken.setString(2, username);
                setExchangeToken.executeUpdate();

                logger.info("UserModel logged in with token: " + exchangeToken);
                return new UserModel(
                        userModel.getUsername(),
                        userModel.getName(),
                        userModel.getPassword(),
                        exchangeToken,
                        userModel.getBio(),
                        userModel.getImage(),
                        userModel.getBalance(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        userModel.getElo(),
                        userModel.getWins(),
                        userModel.getLooses()
                        );
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.error("Password for user " + username + " is wrong.");
        }
        return null;
    }


    /// Sql Inject
    // Service auslagen
    public void addWin(String username) {
        try (PreparedStatement setWinStatistics = this.connection.prepareStatement("UPDATE users SET wins=wins+1, elo=elo+3 WHERE username =?;");) {
            setWinStatistics.setString(1, username);
            setWinStatistics.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    /// Sql Inject
    // Service auslagen
    public void addLoss(String username) {
        try (PreparedStatement setLooseStatistics = this.connection.prepareStatement("UPDATE users SET looses=looses+1, elo=elo-5 WHERE username =?;");) {
            setLooseStatistics.setString(1, username);
            setLooseStatistics.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }


    @Override
    public void addResultSetToArray(List<CardModel> listOfCards, ResultSet rs) throws SQLException {
        if (rs.next()) {
            do {
                if (rs.getString("cardType").equalsIgnoreCase(MonsterType.SPELL.name())) {
                    listOfCards.add(new Spell(rs.getString("uuid"), rs.getString("owner"), null,
                            Type.valueOf(rs.getString("elementType").toUpperCase()),
                            MonsterType.valueOf(rs.getString("cardType").toUpperCase()),
                            rs.getInt("damage")
                    ));
                } else {
                    listOfCards.add(new Monster(rs.getString("uuid"), rs.getString("owner"), null,
                            Type.valueOf(rs.getString("elementType").toUpperCase()),
                            MonsterType.valueOf(rs.getString("cardType").toUpperCase()),
                            rs.getInt("damage")
                    ));
                }
            } while (rs.next());
        }
    }

    @Override
    public List<CardModel> getDeck(String username) {
        List<CardModel> userDeck = new ArrayList<>();
        try (PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM cards WHERE storageType='deck' AND owner=?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            addResultSetToArray(userDeck, rs);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return userDeck;
    }

    @Override
    public UserModel getUserData(String username) {
        UserModel userModel = null;
        try (PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM users WHERE username=?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                logger.info(username + " gets the data!");
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
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return userModel;
    }

    @Override
    public boolean configureDeck(String username, List<String> deck) {
        if (deck.size() != 4) {
            logger.error("Deck has not enough cards!");
            return false;
        }
        for (String uuid : deck) {
            try (PreparedStatement pr = this.connection.prepareStatement("UPDATE cards SET storageType='deck' WHERE uuid=? AND owner=? ;");) {
                pr.setString(1, uuid);
                pr.setString(2, username);
                pr.executeUpdate();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
        logger.info(username + " configured the deck! For the user " + username);
        return true;
    }

    @Override
    public List<CardModel> getStack(String username) {
        List<CardModel> userStack = new ArrayList<>();
        try (PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM cards WHERE storageType='stack' AND owner= ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            addResultSetToArray(userStack, rs);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return userStack;
    }

    @Override
    public boolean addPackage(List<CardModel> packageToAdd) {
        String packageId = AuthenticationService.generateAuthToken();
        for (CardModel c : packageToAdd) {
            try (PreparedStatement ps = this.connection.prepareStatement("INSERT INTO cards (uuid, packageId, cardtype, elementtype, damage, storageType) VALUES(?, ?, ?, ?, ?, ?)");) {
                ps.setString(1, c.getId());
                ps.setString(2, packageId);
                ps.setString(3, c.getMonsterType().name());
                ps.setString(4, c.getElementType().name());
                ps.setDouble(5, c.getDamage());
                ps.setString(6, "package");
                ps.executeUpdate();
                logger.info("Added package with the Id " + packageId + " to DB");
            } catch (SQLException e) {
                logger.error(e.getMessage());
                return false;
            }
        }
        return true;
    }

    @Override
    public List<CardModel> getAllCards(String username) {
        List<CardModel> cardModelList = new ArrayList<>(getStack(username));
        cardModelList.addAll(getDeck(username));
        return cardModelList;
    }

    public StringBuilder getRank() {
        StringBuilder builder = new StringBuilder();
        int i = 1;
        try {
            ResultSet rs = this.stmt.executeQuery("SELECT username, elo, wins, looses FROM users ORDER BY elo DESC");
            if (rs.next()) {
                do {
                    builder.append(i)
                            .append(" ").append(rs.getString("username")).append(" with ")
                            .append(+rs.getInt("elo")).append(" Elo Points ")
                            .append(rs.getInt("wins")).append(" with Wins ")
                            .append(rs.getInt("looses")).append(" and Lost \r\n");
                    i++;
                } while (rs.next());
            } else {
                logger.error("Rank cannot be displayed");
                return builder;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        builder.append("\r\n");
        return builder;
    }
}
