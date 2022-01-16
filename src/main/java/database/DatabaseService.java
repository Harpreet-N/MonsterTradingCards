package database;
import org.apache.log4j.Logger;
import java.sql.*;
public class DatabaseService {

    private static final Logger logger = Logger.getLogger(DatabaseService.class);
    private final Statement stmt;

    public DatabaseService(Statement stmt) {
        this.stmt = stmt;
    }

    private final String USER_QUERY = "CREATE TABLE users (username VARCHAR ( 255 ) PRIMARY KEY, name VARCHAR ( 255 ), password VARCHAR ( 255 ) NOT NULL, token VARCHAR ( 255 ), bio VARCHAR ( 255 ), image VARCHAR ( 255 ), deck VARCHAR ( 255 ), stackModel VARCHAR ( 255 ), balance INTEGER DEFAULT 20, elo INTEGER DEFAULT 100, wins INTEGER DEFAULT 0, looses INTEGER DEFAULT 0)";
    private final String CARD_QUERY = "CREATE TABLE cards (uuid VARCHAR ( 255 ) PRIMARY KEY, owner VARCHAR ( 255 ) NULL, packageId VARCHAR ( 255 ) NULL, cardType VARCHAR ( 255 ) NOT NULL, elementType VARCHAR ( 255 ) NOT NULL, storagetype VARCHAR ( 255 ) NOT NULL, damage DECIMAL(5,2) NOT NULL)";
    private final String STORE_QUERY = "CREATE TABLE store (uuid VARCHAR ( 255 ) PRIMARY KEY, offer VARCHAR ( 255 ) NOT NULL, card_to_trade VARCHAR ( 255 ) NOT NULL, wants_monster BOOLEAN DEFAULT FALSE, wants_spell BOOLEAN DEFAULT FALSE, min_damage INTEGER DEFAULT 0)";
    private final String DROP_ALL_TABLES = "DROP TABLE IF EXISTS users, cards, store";

    public void createTable() {
        this.dropTable();
        try {
            this.stmt.executeUpdate(USER_QUERY);
            this.stmt.executeUpdate(CARD_QUERY);
            this.stmt.executeUpdate(STORE_QUERY);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public void dropTable() {
        try {
            this.stmt.executeUpdate(DROP_ALL_TABLES);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }
}