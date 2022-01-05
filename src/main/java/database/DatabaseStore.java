package database;

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

    public boolean decreaseCoinsByFive(String username) {
        try {
            if (userExists(username)) {
                PreparedStatement setWinStatistics = this.connection.prepareStatement("UPDATE users SET coins=coins-5 WHERE username =?;");

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
/*
    public boolean buyPackage(String username) {
        if (getCoins(username) >= 5) {
            try {
                // Select newest package from database
                ResultSet rs = this.stmt.executeQuery(Queries.SELECT_LATEST_PACKAGE.getQuery());
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
    } */

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
