package database;

import lombok.Getter;
import org.apache.log4j.Logger;

import java.sql.*;

public class Database {

    private final String DB_URL;
    private final String DB_USER;
    private final String DB_PW;
    private final String DRIVER = "org.postgresql.Driver";

    private static final Logger logger = Logger.getLogger(Database.class);

    @Getter
    Connection connection = null;

    @Getter
    Statement stmt = null;

    public Database() {
        this.DB_URL = "jdbc:postgresql://localhost:5432/postgres";
        this.DB_USER = "swe1user";
        this.DB_PW = "swe1pw";
    }

    public void connect() {
        try {
            Class.forName(DRIVER);
            logger.info("Connecting to DB");
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PW);
            this.stmt = connection.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
            logger.error(e.getMessage());
        }
    }

}
