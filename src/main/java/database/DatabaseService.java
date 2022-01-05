package database;

import model.CardModel;
import org.apache.log4j.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {

    private static final Logger logger = Logger.getLogger(DatabaseService.class);
    private final Statement stmt;
    private final Connection connection;

    public DatabaseService(Statement stmt, Connection connection) {
        this.stmt = stmt;
        this.connection = connection;
    }

    public void createTable() {
        this.dropTable();
        try {
            this.stmt.executeUpdate(DatabaseQuery.CARD_QUERY.getQuery());
            this.stmt.executeUpdate(DatabaseQuery.USER_QUERY.getQuery());
            this.stmt.executeUpdate(DatabaseQuery.STORE_QUERY.getQuery());
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public void dropTable() {
        try {
            this.stmt.executeUpdate(DatabaseQuery.DROP_ALL_TABLES.getQuery());
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }
}
