package database;
import org.apache.log4j.Logger;
import java.sql.*;
public class DatabaseService {

    private static final Logger logger = Logger.getLogger(DatabaseService.class);
    private final Statement stmt;

    public DatabaseService(Statement stmt) {
        this.stmt = stmt;
    }

    public void createTable() {
        this.dropTable();
        try {
            this.stmt.executeUpdate("CREATE TABLE users (username VARCHAR ( 255 ) PRIMARY KEY, name VARCHAR ( 255 ), password VARCHAR ( 255 ) NOT NULL, token VARCHAR ( 255 ), bio VARCHAR ( 255 ), image VARCHAR ( 255 ), deck VARCHAR ( 255 ), stackModel VARCHAR ( 255 ), balance INTEGER DEFAULT 20, elo INTEGER DEFAULT 100, wins INTEGER DEFAULT 0, looses INTEGER DEFAULT 0)");
            this.stmt.executeUpdate("CREATE TABLE cards (uuid VARCHAR ( 255 ) PRIMARY KEY, owner VARCHAR ( 255 ) NULL, packageId VARCHAR ( 255 ) NULL, cardType VARCHAR ( 255 ) NOT NULL, elementType VARCHAR ( 255 ) NOT NULL, storageType VARCHAR ( 255 ) NOT NULL, damage DECIMAL(5,2) NOT NULL)");
            this.stmt.executeUpdate("CREATE TABLE store (id VARCHAR ( 255 ) PRIMARY KEY, owner VARCHAR ( 255 ), card_to_trade VARCHAR ( 255 ) NOT NULL, type VARCHAR ( 255 ) NOT NULL , minimum_damage INTEGER DEFAULT 0)");
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public void dropTable() {
        try {
            this.stmt.executeUpdate("DROP TABLE IF EXISTS users, cards, store");
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }
}