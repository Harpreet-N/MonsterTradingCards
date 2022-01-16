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

    // Auch in einer Service Classe
    public void decreaseBalance(String username) {
        try (PreparedStatement setWinStatistics = this.connection.prepareStatement("UPDATE users SET balance=balance-5 WHERE username =?;");) {
            setWinStatistics.setString(1, username);
            setWinStatistics.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    // Auch in einer Service Classe
    public boolean buyPackage(String username) {
        if (getCoins(username) >= 5) {
            String packageId = getLatestPackage();
            if (packageId != null) {
                try (PreparedStatement setPackageBuyer = this.connection.prepareStatement("UPDATE cards SET owner=?, packageId=NULL, storagetype='stack' WHERE packageId=? ;");) {
                    setPackageBuyer.setString(1, username);
                    setPackageBuyer.setString(2, packageId);
                    setPackageBuyer.executeUpdate();

                    decreaseBalance(username);
                    logger.info(username + " got package " + packageId);
                    return true;
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
        } else {
            logger.info(username + " has not enough balance!");
        }

        return false;
    }

    private String getLatestPackage() {
        try (ResultSet rs = this.stmt.executeQuery(DatabaseQuery.SELECT_LATEST_PACKAGE.getQuery());) {
            if (!rs.next()) {
                logger.info("There is no package");
                return null;
            } else {
                return rs.getString("packageId");
            }
        } catch (SQLException e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    public int getCoins(String username) {
        try (PreparedStatement ps = this.connection.prepareStatement(DatabaseQuery.SELECT_FETCH_COINS.getQuery());) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.getInt("balance");
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return 0;
    }

    public StringBuilder getAllTrade() {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n----  Trade ----\r\n");

        try {
            ResultSet rs = this.stmt.executeQuery(DatabaseQuery.SELECT_ALL_TRADING_DEALS.getQuery());
            if (rs.next()) {
                int i = 1;
                do {
                    sb.append(i)
                            .append(". Offerer: ")
                            .append(rs.getString("offer"))
                            .append(", Card to trade: ")
                            .append(rs.getString("card_to_trade"))
                            .append(", Min Damage: ")
                            .append(rs.getString("min_damage"))
                            .append(", want Monster: ")
                            .append(rs.getBoolean("wants_monster"))
                            .append(", want Spell: ")
                            .append(rs.getBoolean("wants_spell"))
                            .append("\r\n");
                } while (rs.next());
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        sb.append("\r\n-----------\r\n");
        return sb;
    }

    private boolean isCardInDeck(String uuid) {
        try (PreparedStatement ps = this.connection.prepareStatement(DatabaseQuery.SELECT_STORAGE_BY_UUID.getQuery());) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getString("storagetype").equals("deck")) {
                    return true;
                }
            } else {
                logger.error("Card not found");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    public boolean pushTradingDeal(String username, String tradeUUID, String cardOfferUUID, double minDamage, boolean wantsMonster, boolean wantsSpell) {
        if (userExists(username) && checkIfCardExists(cardOfferUUID) && !isCardInDeck(cardOfferUUID) && userOwnsCard(username, cardOfferUUID)) {

            // Lock card
            lockCard(cardOfferUUID);

            try (PreparedStatement tradingStmt = this.connection.prepareStatement("INSERT INTO store (uuid, offer, card_to_trade, wants_monster, wants_spell, min_damage) VALUES(?, ?, ?, ?, ?, ?)");) {
                tradingStmt.setString(1, tradeUUID);
                tradingStmt.setString(2, username);
                tradingStmt.setString(3, cardOfferUUID);
                tradingStmt.setBoolean(4, wantsMonster);
                tradingStmt.setBoolean(5, wantsSpell);
                tradingStmt.setDouble(6, minDamage);
                tradingStmt.executeUpdate();

                logger.info(username + " completed a trade: " + tradeUUID);
                return true;
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.error("User or Card does not exist!");
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


    // SQL INJECT
    private CardModel getCardByUUID(String uuid) {
        CardModel c = null;
        try (PreparedStatement ps = this.connection.prepareStatement(DatabaseQuery.SELECT_CARD_BY_UUID.getQuery());) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                logger.error("Card does not exist");
            } else {
                c = createCardFromResult(rs);
            }
            rs.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return c;
    }

    private CardModel createCardFromResult(ResultSet rs) throws SQLException {
        CardModel c = null;

        if (rs.getString("cardtype").equals(MonsterType.SPELL.name())) {
            // CardModel is spell
            c = new Spell(rs.getString("uuid"), rs.getString("owner"), null, Type.valueOf(rs.getString("elementtype").toUpperCase()),
                    MonsterType.valueOf(rs.getString("cardtype").toUpperCase()),
                    rs.getInt("damage")
            );
        } else {
            // CardModel is monster
            c = new Monster(rs.getString("uuid"), rs.getString("owner"), null,
                    Type.valueOf(rs.getString("elementtype").toUpperCase()),
                    MonsterType.valueOf(rs.getString("cardtype").toUpperCase()),
                    rs.getInt("damage")
            );

        }
        return c;
    }

    private void removeTrade(String tradeUUID) {
        try (PreparedStatement ps = this.connection.prepareStatement(DatabaseQuery.DELETE_TRADE_BY_UUID.getQuery());) {
            ps.setString(1, tradeUUID);
            ps.executeQuery();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private void changeOwnerOfCard(String username, String cardUUID) {
        try (PreparedStatement setOwner = this.connection.prepareStatement("UPDATE cards SET owner=? WHERE uuid=? ;")) {
            setOwner.setString(1, username);
            setOwner.setString(2, cardUUID);
            setOwner.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }


    private void lockCard(String cardUUID) {
        if (checkIfCardExists(cardUUID)) {
            try (PreparedStatement setPackageBuyer = this.connection.prepareStatement("UPDATE cards SET locked=TRUE WHERE uuid =?;");) {
                setPackageBuyer.setString(1, cardUUID);
                setPackageBuyer.executeUpdate();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.error("CardModel does not exist");
        }
    }

    private void unlockCard(String cardUUID) {
        if (checkIfCardExists(cardUUID)) {
            try (PreparedStatement setPackageBuyer = this.connection.prepareStatement("UPDATE cards SET locked=FALSE WHERE uuid =?;");) {
                setPackageBuyer.setString(1, cardUUID);
                setPackageBuyer.executeUpdate();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.error("CardModel does not exist");
        }
    }

    private boolean userPublishedTrade(String username, String tradeUUID) {
        try {
            ResultSet rs = stmt.executeQuery(DatabaseQuery.SELECT_ALL_TRADING_DEALS.getQuery());
            while (rs.next()) {
                if (rs.getString("offer").equals(username) && rs.getString("uuid").equals(tradeUUID)) {
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


    // SQL INJECT
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
                            rs.getString("offer"),
                            rs.getString("card_to_trade"),
                            rs.getInt("min_damage"),
                            rs.getBoolean("wants_monster"),
                            rs.getBoolean("wants_spell"));

                    offer = getCardByUUID(t.getCardToTrade());

                    if (counterOffer.getDamage() >= t.getMinDamage()) {
                        if (t.isWantsSpell() && counterOffer.getMonsterType() == MonsterType.SPELL) {
                            // Accept trade - wants spell - counter offer is spell
                            changeOwnerOfCard(username, t.getCardToTrade());
                            changeOwnerOfCard(offer.getOwner(), counterofferUUID);

                            removeTrade(t.getUuid());
                            unlockCard(t.getCardToTrade());

                            logger.info(username + " accepted trading deal " + tradeUUID);
                            return true;
                        } else if (t.isWantsMonster() && !(counterOffer.getMonsterType() == MonsterType.SPELL)) {
                            changeOwnerOfCard(username, t.getCardToTrade());
                            changeOwnerOfCard(offer.getOwner(), counterofferUUID);

                            removeTrade(t.getUuid());
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


    // SQL INJECT
    public boolean deleteTradeByUser(String username, String tradeUUID) {
        try {
            if (this.userExists(username)) {
                ResultSet rs = stmt.executeQuery(DatabaseQuery.SELECT_TRADE_WHERE_UUID.getQuery() + tradeUUID + "'");

                if (rs.next()) {
                    String offer = rs.getString("offer");
                    String cardUUID = rs.getString("card_to_trade");

                    if (offer.equals(username)) {
                        removeTrade(tradeUUID);
                        unlockCard(cardUUID);

                        logger.info(username + " delete deal " + tradeUUID);
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
