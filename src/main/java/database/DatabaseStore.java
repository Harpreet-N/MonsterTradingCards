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
        try (PreparedStatement stm = this.connection.prepareStatement("UPDATE users SET balance=balance-5 WHERE username =?;");) {
            stm.setString(1, username);
            stm.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    // Auch in einer Service Classe
    public boolean buyPackage(String username) {
        if (getCoins(username) >= 5) {
            String packageId = getLatestPackage();
            if (packageId != null) {
                try (PreparedStatement stm = this.connection.prepareStatement("UPDATE cards SET owner=?, packageId=NULL, storagetype='stack' WHERE packageId=? ;");) {
                    stm.setString(1, username);
                    stm.setString(2, packageId);
                    stm.executeUpdate();

                    decreaseBalance(username);
                    logger.info(username + " got package " + packageId);
                    return true;
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
        } else {
            logger.info(username + " has not enough balance!");
            return false;
        }

        return true;
    }

    private String getLatestPackage() {
        try (ResultSet rs = this.stmt.executeQuery("SELECT packageId FROM cards WHERE storagetype='package' LIMIT 1");) {
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
        try (PreparedStatement stm = this.connection.prepareStatement("SELECT balance FROM users WHERE username= ?");) {
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            rs.next();
            return rs.getInt("balance");
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return 0;
    }

    public StringBuilder getAllTrade() {
        StringBuilder sb = new StringBuilder();
        try {
            ResultSet rs = this.stmt.executeQuery(DatabaseQuery.SELECT_ALL_TRADING_DEALS.getQuery());
            if (rs.next()) {
                int i = 1;
                do {
                    sb.append(i)
                            .append(". Offer: ").append(rs.getString("offer"))
                            .append(", Card to trade: ").append(rs.getString("card_to_trade"))
                            .append(", Min Damage: ").append(rs.getString("min_damage"))
                            .append(", want Monster: ").append(rs.getBoolean("wants_monster"))
                            .append(", want Spell: ").append(rs.getBoolean("wants_spell"))
                            .append("\r\n");
                } while (rs.next());
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        sb.append("\r\n");
        return sb;
    }

    private boolean isCardInDeck(String uuid) {
        try (PreparedStatement ps = this.connection.prepareStatement("SELECT storagetype FROM cards WHERE uuid= ?");) {
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
        if (checkIfCardExists(cardOfferUUID) && !isCardInDeck(cardOfferUUID) && userOwnsCard(username, cardOfferUUID)) {

            try (PreparedStatement stm = this.connection.prepareStatement("INSERT INTO store (uuid, offer, card_to_trade, wants_monster, wants_spell, min_damage) VALUES(?, ?, ?, ?, ?, ?)");) {
                stm.setString(1, tradeUUID);
                stm.setString(2, username);
                stm.setString(3, cardOfferUUID);
                stm.setBoolean(4, wantsMonster);
                stm.setBoolean(5, wantsSpell);
                stm.setDouble(6, minDamage);
                stm.executeUpdate();

                logger.info(username + " completed the trade: " + tradeUUID);
                return true;
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.error("Something does not exists");
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
        try (PreparedStatement stm = this.connection.prepareStatement("SELECT * FROM cards WHERE uuid=?");) {
            stm.setString(1, uuid);
            ResultSet rs = stm.executeQuery();
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

        if (rs.getString("cardType").equals(MonsterType.SPELL.name())) {
            c = new Spell(rs.getString("uuid"), rs.getString("owner"), null, Type.valueOf(rs.getString("elementType").toUpperCase()),
                    MonsterType.valueOf(rs.getString("cardType").toUpperCase()),
                    rs.getInt("damage")
            );
        } else {
            c = new Monster(rs.getString("uuid"), rs.getString("owner"), null,
                    Type.valueOf(rs.getString("elementType").toUpperCase()),
                    MonsterType.valueOf(rs.getString("cardType").toUpperCase()),
                    rs.getInt("damage")
            );

        }
        return c;
    }

    private void removeTrade(String tradeUUID) {
        try (PreparedStatement ps = this.connection.prepareStatement("DELETE FROM store WHERE uuid=?");) {
            ps.setString(1, tradeUUID);
            ps.executeQuery();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private void changeOwnerOfCard(String username, String cardUUID) {
        try (PreparedStatement stm = this.connection.prepareStatement("UPDATE cards SET owner=? WHERE uuid=? ;")) {
            stm.setString(1, username);
            stm.setString(2, cardUUID);
            stm.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
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


    // Trading need a new logic

    // SQL INJECT
    // Does not work
    public boolean acceptTradingDeal(String username, String tradeUUID, String counterofferUUID) {
        Trade t;
        CardModel offer;
        CardModel counterOffer;

        try {
            if (!userPublishedTrade(username, tradeUUID) && checkIfCardExists(counterofferUUID)) {
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

                            logger.info(username + " accepted trading deal " + tradeUUID);
                            return true;
                        } else if (t.isWantsMonster() && !(counterOffer.getMonsterType() == MonsterType.SPELL)) {
                            changeOwnerOfCard(username, t.getCardToTrade());
                            changeOwnerOfCard(offer.getOwner(), counterofferUUID);

                            removeTrade(t.getUuid());

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
    // Does not work
    public boolean deleteTradeByUser(String username, String tradeUUID) {
        try {
            ResultSet rs = stmt.executeQuery(DatabaseQuery.SELECT_TRADE_WHERE_UUID.getQuery() + tradeUUID + "'");

            if (rs.next()) {
                String offer = rs.getString("offer");
                if (offer.equals(username)) {
                    removeTrade(tradeUUID);
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
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return false;
    }
}
