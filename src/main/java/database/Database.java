package database;

import lombok.Getter;
import org.apache.log4j.Logger;

import java.sql.*;

public class Database {

    private static final Logger logger = Logger.getLogger(Database.class);

    @Getter
    Connection connection = null;

    @Getter
    Statement stmt = null;

    public Database() {
    }

    public void connect() {
        try {
            logger.info("Connect to DB");
            this.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "swe1user", "swe1pw");
            this.stmt = connection.createStatement();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

}
