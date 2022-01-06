package database;

import model.CardModel;
import model.Monster;
import model.Spell;
import model.UserModel;
import model.helper.MonsterType;
import model.helper.Type;
import model.store.Trade;
import org.apache.log4j.Logger;
import util.Authentication;
import util.CardUtil;

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
                registerUserStmt.setString(2, Authentication.hashPassword(password));
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
                    if (Authentication.passwordIsEqual(password, rs.getString("password"))) {

                        try (PreparedStatement setExchangeToken = this.connection.prepareStatement("UPDATE users SET token= ? WHERE username = ? ;");) {
                            setExchangeToken.setString(1, exchangeToken);
                            setExchangeToken.setString(2, username);
                            setExchangeToken.executeUpdate();

                            logger.info("UserModel logged in with token: " + exchangeToken);
                            return new UserModel(
                                    rs.getString("username"),
                                    rs.getString("password"),
                                    exchangeToken,
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


    public int getElo(String username) {
        try {
            if (userExists(username)) {
                ResultSet rs = this.stmt.executeQuery(DatabaseQuery.SELECT_FETCH_ELO.getQuery() + username + "'");
                rs.next();
                return rs.getInt("elo");
            } else {
                logger.info("Error: UserModel does not exist!");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return 0;
    }

    public int getWins(String username) {
        try {
            if (userExists(username)) {
                ResultSet rs = this.stmt.executeQuery(DatabaseQuery.SELECT_FETCH_ELO.getQuery() + username + "'");

                rs.next();

                return rs.getInt("wins");
            } else {
                logger.error("Error: UserModel does not exist!");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return 0;
    }

    public int getLooses(String username) {
        try {
            if (userExists(username)) {
                ResultSet rs = this.stmt.executeQuery(DatabaseQuery.SELECT_FETCH_ELO.getQuery() + username + "'");

                rs.next();

                return rs.getInt("looses");
            } else {
                logger.error("Error: UserModel does not exist!");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return 0;
    }

    public int getCoins(String username) {
        try {
            if (userExists(username)) {
                ResultSet rs = this.stmt.executeQuery(DatabaseQuery.SELECT_FETCH_COINS.getQuery() + username + "'");

                rs.next();

                return rs.getInt("balance");
            } else {
                logger.error("Error: UserModel does not exist!");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return 0;
    }

    public void addWin(String username) {
        try {
            if (userExists(username)) {
                PreparedStatement setWinStatistics = this.connection.prepareStatement("UPDATE users SET wins=wins+1, elo=elo+3 WHERE username =?;");
                setWinStatistics.setString(1, username);
                setWinStatistics.executeUpdate();
            } else {
                logger.error("Error: UserModel does not exist!");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public void addLoss(String username) {
        try {
            if (userExists(username)) {
                PreparedStatement setLooseStatistics = this.connection.prepareStatement("UPDATE users SET looses=looses+1, elo=elo-5 WHERE username =?;");
                setLooseStatistics.setString(1, username);
                setLooseStatistics.executeUpdate();
            } else {
                logger.error("Error: UserModel does not exist!");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public boolean decreaseCoinsByFive(String username) {
        try {
            if (userExists(username)) {
                PreparedStatement setWinStatistics = this.connection.prepareStatement("UPDATE users SET balance=balance-5 WHERE username =?;");
                setWinStatistics.setString(1, username);
                setWinStatistics.executeUpdate();

                return true;

            } else {
                logger.error("UserModel does not exist!");
                return false;
            }
        } catch (SQLException e) {
            logger.info(e.getMessage());
        }

        return false;
    }


    public boolean checkIfCardExists(String uuid) {
        try {
            ResultSet rs = stmt.executeQuery(DatabaseQuery.SELECT_ALL_CARDS.getQuery());
            while (rs.next()) {
                if (rs.getString("uuid").equals(uuid)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    /* DB Helper functions */

    private CardModel transformResultSetInCard(ResultSet rs) throws SQLException {
        CardModel c = null;

        if (rs.getString("cardtype").equals(MonsterType.SPELL.name())) {
            // CardModel is spell
            c = new Spell(
                    rs.getString("uuid"),
                    rs.getString("owner"),
                    null,
                    Type.valueOf(rs.getString("elementtype").toUpperCase()),
                    MonsterType.valueOf(rs.getString("cardtype").toUpperCase()),
                    rs.getInt("damage")
            );
        } else {
            // CardModel is monster
            c = new Monster(
                    rs.getString("uuid"),
                    rs.getString("owner"),
                    null,
                    Type.valueOf(rs.getString("elementtype").toUpperCase()),
                    MonsterType.valueOf(rs.getString("cardtype").toUpperCase()),
                    rs.getInt("damage")
            );

        }
        return c;
    }

    private void addResultSetToArray(List<CardModel> listOfCards, ResultSet rs) throws SQLException {
        if (rs.next()) {
            do {
                if (rs.getString("cardtype").equalsIgnoreCase(MonsterType.SPELL.name())) {
                    // CardModel is spell
                    listOfCards.add(new Spell(
                            rs.getString("uuid"),
                            rs.getString("owner"),
                            null,
                            Type.valueOf(rs.getString("elementtype").toUpperCase()),
                            MonsterType.valueOf(rs.getString("cardtype").toUpperCase()),
                            rs.getInt("damage")
                    ));
                } else {
                    // CardModel is monster
                    listOfCards.add(new Monster(
                            rs.getString("uuid"),
                            rs.getString("owner"),
                            null,
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
    /* End DB Helper functions */

    /* Deck functionality */
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
        UserModel u = null;
        try {
            if (userExists(username)) {
                ResultSet rs = this.stmt.executeQuery(DatabaseQuery.SELECT_BY_USERNAME.getQuery() + username + "'");

                if (rs.next()) {
                    logger.info(username + " has requested his user data!");
                    return new UserModel(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("token"),
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

        return u;
    }

    public boolean configureDeck(String username, List<String> deck) {
        if (deck.size() != 4) {
            logger.error("Deck has too many or too less cards!");
            return false;
        }

        try {
            if (userExists(username)) {
                for (String uuid : deck) {
                    if (!checkIfCardIsLocked(uuid)) {
                        PreparedStatement setPackageBuyer = this.connection.prepareStatement("UPDATE cards SET storagetype='deck' WHERE uuid=? AND owner=? ;");
                        setPackageBuyer.setString(1, uuid);
                        setPackageBuyer.setString(2, username);
                        setPackageBuyer.executeUpdate();
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
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return false;
    }


    public void addCardsToStack(String username, List<CardModel> stackToAdd) {
        try {
            if (userExists(username)) {
                for (CardModel c : stackToAdd) {
                    PreparedStatement insertCardIntoDB = this.connection.prepareStatement("INSERT INTO cards (uuid, owner, cardtype, elementtype, storagetype, damage) VALUES(?, ?, ?, ?, ?, ?)");


                    insertCardIntoDB.setString(1, c.getId());
                    insertCardIntoDB.setString(2, username);
                    insertCardIntoDB.setString(3, c.getMonsterType().name());
                    insertCardIntoDB.setString(4, c.getElementType().name());
                    insertCardIntoDB.setString(5, "stack");
                    insertCardIntoDB.setDouble(6, c.getDamage());

                    insertCardIntoDB.executeUpdate();
                }
            } else {
                logger.error("Error: UserModel does not exist!");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
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

    public int getStackSize(String username) {
        List<CardModel> stack = getStack(username);

        return stack.size();
    }
    /* End Stack functionality */

    /* Start package functionality */
    public boolean addPackage(List<CardModel> packageToAdd) {
        String packageId = Authentication.generateAuthToken();

        try {
            for (CardModel c : packageToAdd) {
                PreparedStatement insertPackageIntoDB = this.connection.prepareStatement("INSERT INTO cards (uuid, packageId, cardtype, elementtype, damage, storagetype) VALUES(?, ?, ?, ?, ?, ?)");

                insertPackageIntoDB.setString(1, c.getId());
                insertPackageIntoDB.setString(2, packageId);
                insertPackageIntoDB.setString(3, c.getMonsterType().name());
                insertPackageIntoDB.setString(4, c.getElementType().name());
                insertPackageIntoDB.setDouble(5, c.getDamage());
                insertPackageIntoDB.setString(6, "package");

                insertPackageIntoDB.executeUpdate();
            }

            logger.info("Added package " + packageId + " to DB");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return false;
    }


    public boolean buyPackage(String username) {
        if (getCoins(username) >= 5) {
            try {
                // Select newest package from database
                ResultSet rs = this.stmt.executeQuery(DatabaseQuery.SELECT_LATEST_PACKAGE.getQuery());
                if (!rs.next()) {
                    logger.error("No packages exist");
                } else {
                    String packageId = rs.getString("packageId");

                    PreparedStatement setPackageBuyer = this.connection.prepareStatement("UPDATE cards SET owner=?, packageId=NULL, storagetype='stack' WHERE packageId=? ;");
                    setPackageBuyer.setString(1, username);
                    setPackageBuyer.setString(2, packageId);
                    setPackageBuyer.executeUpdate();

                    decreaseCoinsByFive(username);
                    logger.info(username + " bought package " + packageId);
                    return true;
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.error(username + " has insufficient balance!");
        }

        return false;
    }
    /* End package functionality */

    public List<CardModel> getAllCards(String username) {
        List<CardModel> allCards = new ArrayList<>(getStack(username));
        allCards.addAll(getDeck(username));

        return allCards;
    }

    /* Scoreboard functionality */
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
    /* End Scoreboard functionality */

    /* Trading functionality */
    private boolean isCardInDeck(String uuid) {
        try {
            ResultSet rs = stmt.executeQuery(DatabaseQuery.SELECT_STORAGE_BY_UUID.getQuery() + uuid + "'");
            if (rs.next()) {
                if (rs.getString("storagetype").equals("deck")) {
                    return true;
                }
            } else {
                logger.error("CardModel not found");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    public StringBuilder retrieveAllTradingDeals() {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n---- Trading Deals ----\r\n");

        try {
            ResultSet rs = this.stmt.executeQuery(DatabaseQuery.SELECT_ALL_TRADING_DEALS.getQuery());

            if (rs.next()) {
                int i = 1;
                do {
                    sb
                            .append(i)
                            .append(". Offerer: ")
                            .append(rs.getString("offerer"))
                            .append(", CardModel to trade: ")
                            .append(rs.getString("card_to_trade"))
                            .append(", Minimum Damage: ")
                            .append(rs.getString("min_damage"))
                            .append(", Wants Monster: ")
                            .append(rs.getBoolean("wants_monster"))
                            .append(", Wants Spell: ")
                            .append(rs.getBoolean("wants_spell"))
                            .append("\r\n");
                } while (rs.next());
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        sb.append("\r\n----------------------\r\n");

        return sb;
    }

    public List<Trade> getAllTradingDeals() {
        List<Trade> tradingDeals = new ArrayList<>();

        try {
            ResultSet rs = this.stmt.executeQuery(DatabaseQuery.SELECT_ALL_TRADING_DEALS.getQuery());

            if (rs.next()) {

                do {
                    tradingDeals.add(new Trade(
                            rs.getString("uuid"),
                            rs.getString("offerer"),
                            rs.getString("card_to_trade"),
                            rs.getDouble("min_damage"),
                            rs.getBoolean("wants_monster"),
                            rs.getBoolean("wants_spell")
                    ));
                } while (rs.next());

                return tradingDeals;
            } else {
                logger.error("No trading deals found");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    public boolean pushTradingDeal(String username, String tradeUUID, String cardOfferUUID, double minDamage, boolean wantsMonster, boolean wantsSpell) {
        try {
            if (userExists(username) &&
                    checkIfCardExists(cardOfferUUID) &&
                    !isCardInDeck(cardOfferUUID) &&
                    userOwnsCard(username, cardOfferUUID)) {

                // Lock card
                lockCard(cardOfferUUID);

                // Insert trade into deals
                PreparedStatement tradingDealStatement = this.connection.prepareStatement("INSERT INTO store (uuid, offerer, card_to_trade, wants_monster, wants_spell, min_damage) VALUES(?, ?, ?, ?, ?, ?)");
                tradingDealStatement.setString(1, tradeUUID);
                tradingDealStatement.setString(2, username);
                tradingDealStatement.setString(3, cardOfferUUID);
                tradingDealStatement.setBoolean(4, wantsMonster);
                tradingDealStatement.setBoolean(5, wantsSpell);
                tradingDealStatement.setDouble(6, minDamage);
                tradingDealStatement.executeUpdate();

                logger.info(username + " pushed a trading deal: " + tradeUUID);
                return true;
            } else {
                logger.error("UserModel or card does not exist, CardModel is in current deck or use is not owner of the card!");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    private boolean userOwnsCard(String owner, String cardOfferUUID) {
        try {
            ResultSet rs = stmt.executeQuery(DatabaseQuery.SELECT_ALL_CARDS.getQuery());
            while (rs.next()) {
                if (rs.getString("owner").equals(owner) && rs.getString("uuid").equals(cardOfferUUID)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    private CardModel getCardByUUID(String uuid) {
        CardModel c = null;
        try {
            ResultSet rs = stmt.executeQuery(DatabaseQuery.SELECT_CARD_BY_UUID.getQuery() + uuid + "'");
            if (!rs.next()) {
                logger.error("CardModel does not exist");
            } else {
                c = transformResultSetInCard(rs);
            }
            rs.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return c;
    }

    private void deleteTrade(String tradeUUID) {
        try {
            this.stmt.executeUpdate(DatabaseQuery.DELETE_TRADE_BY_UUID.getQuery() + tradeUUID + "'");
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private void changeOwnerOfCard(String username, String cardUUID) {
        try {
            if (this.userExists(username)) {
                PreparedStatement setOwner = this.connection.prepareStatement("UPDATE cards SET owner=? WHERE uuid=? ;");
                setOwner.setString(1, username);
                setOwner.setString(2, cardUUID);
                setOwner.executeUpdate();
                setOwner.close();
            } else {
                logger.info("UserModel does not exist.");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }


    private void lockCard(String cardUUID) {
        try {
            if (checkIfCardExists(cardUUID)) {
                PreparedStatement setPackageBuyer = this.connection.prepareStatement("UPDATE cards SET locked=TRUE WHERE uuid =?;");
                setPackageBuyer.setString(1, cardUUID);
                setPackageBuyer.executeUpdate();
            } else {
                logger.error("CardModel does not exist");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private void unlockCard(String cardUUID) {
        try {
            if (checkIfCardExists(cardUUID)) {
                PreparedStatement setPackageBuyer = this.connection.prepareStatement("UPDATE cards SET locked=FALSE WHERE uuid =?;");
                setPackageBuyer.setString(1, cardUUID);
                setPackageBuyer.executeUpdate();
            } else {
                logger.error("CardModel does not exist");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private boolean userPublishedTrade(String username, String tradeUUID) {
        try {
            ResultSet rs = stmt.executeQuery(DatabaseQuery.SELECT_ALL_TRADING_DEALS.getQuery());
            while (rs.next()) {
                if (rs.getString("offerer").equals(username) && rs.getString("uuid").equals(tradeUUID)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return false;
    }


    public boolean editUserData(String userToEdit, String name) {
        try {
            if (this.userExists(userToEdit)) {
                ResultSet rs = stmt.executeQuery(DatabaseQuery.SELECT_BY_USERNAME.getQuery() + userToEdit + "'");

                if (rs.next()) {
                    // Insert updated name, bio and image
                    PreparedStatement setUserData = this.connection.prepareStatement("UPDATE users SET name=? WHERE username=? ;");
                    setUserData.setString(1, name);
                    setUserData.setString(2, userToEdit);

                    setUserData.executeUpdate();
                    setUserData.close();

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

    public boolean deleteTradeByUser(String username, String tradeUUID) {
        try {
            if (this.userExists(username)) {
                ResultSet rs = stmt.executeQuery(DatabaseQuery.SELECT_TRADE_WHERE_UUID.getQuery() + tradeUUID + "'");

                if (rs.next()) {
                    String offerer = rs.getString("offerer");
                    String cardUUID = rs.getString("card_to_trade");

                    if (offerer.equals(username)) {
                        // Trade deleten
                        deleteTrade(tradeUUID);

                        // Karte des Users noch unlocken
                        unlockCard(cardUUID);

                        logger.info(username + " delted trading deal " + tradeUUID);
                    } else {
                        logger.error("Trade is not created by user");
                        return false;
                    }

                    return true;
                } else {
                    logger.error("ResultSet is empty!");
                    return false;
                }
            } else {
                logger.error("UserModel does not exist.");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    public boolean acceptTradingDeal(String username, String tradeUUID, String counterofferUUID) {
        Trade t;
        CardModel offer;
        CardModel counterOffer;

        try {
            if (userExists(username) && !userPublishedTrade(username, tradeUUID) && checkIfCardExists(counterofferUUID)) {
                counterOffer = getCardByUUID(counterofferUUID);
                ResultSet rs = stmt.executeQuery(DatabaseQuery.SELECT_TRADE_WHERE_UUID.getQuery() + tradeUUID + "'");

                if (!rs.next()) {
                    logger.error("Trade does not exist");
                } else {
                    t = new Trade(
                            rs.getString("uuid"),
                            rs.getString("offerer"),
                            rs.getString("card_to_trade"),
                            rs.getInt("min_damage"),
                            rs.getBoolean("wants_monster"),
                            rs.getBoolean("wants_spell"));

                    offer = getCardByUUID(t.getCardToTrade());

                    if (counterOffer.getDamage() >= t.getMinDamage()) {
                        if (t.isWantsSpell() && CardUtil.cardIsSpell(counterOffer)) {
                            // Accept trade - wants spell - counter offer is spell
                            changeOwnerOfCard(username, t.getCardToTrade());
                            changeOwnerOfCard(offer.getOwner(), counterofferUUID);

                            deleteTrade(t.getUuid());
                            unlockCard(t.getCardToTrade());

                            logger.info(username + " accepted trading deal " + tradeUUID);
                            return true;
                        } else if (t.isWantsMonster() && !CardUtil.cardIsSpell(counterOffer)) {
                            changeOwnerOfCard(username, t.getCardToTrade());
                            changeOwnerOfCard(offer.getOwner(), counterofferUUID);

                            deleteTrade(t.getUuid());
                            unlockCard(t.getCardToTrade());

                            logger.info(username + " accepted trading deal " + tradeUUID);
                            return true;
                        } else {
                            logger.error("CardModel type not valid please choose another card");
                        }
                    } else {
                        logger.error("Counter offer lower than minimum damage");
                    }
                }

            } else {
                logger.error("User does not exist, user can't trade with himself or card does not exist!");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return false;
    }
}
