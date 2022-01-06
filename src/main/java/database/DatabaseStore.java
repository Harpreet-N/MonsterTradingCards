package database;

import model.CardModel;
import model.Monster;
import model.Spell;
import model.helper.MonsterType;
import model.helper.Type;
import model.store.Trade;
import org.apache.log4j.Logger;
import service.RandomService;

import java.sql.*;

public class DatabaseStore {

    private static final Logger logger = Logger.getLogger(DatabaseStore.class);

    private final Statement stmt;
    private final Connection connection;

    public DatabaseStore(Statement stmt, Connection connection) {
        this.stmt = stmt;
        this.connection = connection;
    }

    public boolean decreaseCoinsByFive(String username) {
        try {
            if (userExists(username)) {
                PreparedStatement setWinStatistics = this.connection.prepareStatement("UPDATE users SET balance=balance-5 WHERE username =?;");
                setWinStatistics.setString(1, username);
                setWinStatistics.executeUpdate();

                return true;
            } else {
                logger.info("User does not exist!");
                return false;
            }
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
                    logger.info("No packages exist");
                } else {
                    String packageId = rs.getString("packageId");

                    PreparedStatement setPackageBuyer = this.connection.prepareStatement("UPDATE cards SET owner=?, packageId=NULL, storagetype='stack' WHERE packageId=? ;");
                    setPackageBuyer.setString(1, username);
                    setPackageBuyer.setString(2, packageId);
                    setPackageBuyer.executeUpdate();

                    decreaseCoinsByFive(username);
                    logger.error(username + " bought package " + packageId);
                    return true;
                }
            } catch (SQLException e) {
                logger.info(e.getMessage());
            }
        } else {
            logger.info(username + " has insufficient balance!");
        }

        return false;
    }

    public int getCoins(String username) {
        try {
            if (userExists(username)) {
                ResultSet rs = this.stmt.executeQuery(DatabaseQuery.SELECT_FETCH_COINS.getQuery() + username + "'");
                rs.next();

                return rs.getInt("balance");
            } else {
                logger.error("Error: User does not exist!");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return 0;
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
                        if (t.isWantsSpell() && RandomService.cardIsSpell(counterOffer)) {
                            // Accept trade - wants spell - counter offer is spell
                            changeOwnerOfCard(username, t.getCardToTrade());
                            changeOwnerOfCard(offer.getOwner(), counterofferUUID);

                            deleteTrade(t.getUuid());
                            unlockCard(t.getCardToTrade());

                            logger.info(username + " accepted trading deal " + tradeUUID);
                            return true;
                        } else if (t.isWantsMonster() && !RandomService.cardIsSpell(counterOffer)) {
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
