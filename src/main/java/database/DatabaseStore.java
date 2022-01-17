package database;

import model.store.Trade;
import org.apache.log4j.Logger;

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
    public boolean buyPackage(String username) {
        if (getCoins(username) >= 5) {
            String packageId = getPackage();
            if (packageId != null) {
                try (PreparedStatement stm = this.connection.prepareStatement("UPDATE cards SET owner=?, packageId=NULL, storageType='stack' WHERE packageId=? ;");) {
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
            return true;
        } else {
            logger.info(username + " has not enough balance!");
            return false;
        }
    }

    public void decreaseBalance(String username) {
        try (PreparedStatement stm = this.connection.prepareStatement("UPDATE users SET balance=balance-5 WHERE username =?;");) {
            stm.setString(1, username);
            stm.executeUpdate();
            logger.info(username + " decreased Balance");
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private String getPackage() {
        try (ResultSet rs = this.stmt.executeQuery("SELECT packageId FROM cards WHERE storageType='package' LIMIT 1");) {
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
            ResultSet rs = this.stmt.executeQuery("SELECT * FROM store");
            if (rs.next()) {
                while (rs.next()) {
                    sb.append(". Owner: ").append(rs.getString("owner"))
                            .append(" Card to trade: ").append(rs.getString("card_to_trade"))
                            .append(" Type: ").append(rs.getString("type"))
                            .append(" Damage: ").append(rs.getBoolean("minimum_damage"))
                            .append("\r\n");
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        sb.append("\r\n");
        return sb;
    }

    private boolean isCardInDeck(String uuid) {
        try (PreparedStatement ps = this.connection.prepareStatement("SELECT storageType FROM cards WHERE uuid= ?");) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getString("storageType").equals("deck")) {
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

    public boolean startTrade(String username, Trade trade) {
        if (checkIfCardExists(trade.getCardToTrade()) && !isCardInDeck(trade.getId()) && userOwnsCard(username, trade.getCardToTrade())) {
            try (PreparedStatement stm = this.connection.prepareStatement("INSERT INTO store (id, owner, card_to_trade, type, minimum_damage) VALUES(?, ?, ?, ?, ?)");) {
                stm.setString(1, trade.getId());
                stm.setString(2, username);
                stm.setString(3, trade.getCardToTrade());
                stm.setString(4, trade.getType());
                stm.setDouble(5, trade.getMinDamage());
                stm.executeUpdate();
                logger.info(username + "go the trade with the id" + trade.getId());
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
            ResultSet rs = stmt.executeQuery("SELECT * FROM card");
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

    public boolean removeTrade(String tradeUUID) {
        try (PreparedStatement ps = this.connection.prepareStatement("DELETE FROM store WHERE id=?");) {
            ps.setString(1, tradeUUID);
            ps.executeQuery();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return true;
    }

    public boolean changeOwnerOfCard(String username, String cardUUID) {
        try (PreparedStatement stm = this.connection.prepareStatement("UPDATE cards SET owner=? WHERE uuid=? ;")) {
            stm.setString(1, username);
            stm.setString(2, cardUUID);
            stm.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public boolean checkIfCardExists(String uuid) {
        try (PreparedStatement stm = this.connection.prepareStatement("SELECT * FROM cards where uuid= ?;");) {
            stm.setString(1, uuid);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getString("uuid") != null;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return false;
    }
}
