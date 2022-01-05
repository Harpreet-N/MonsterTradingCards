package database;

import model.UserModel;
import org.apache.log4j.Logger;
import util.Authentication;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseUser {

    private static final Logger logger = Logger.getLogger(DatabaseUser.class);

    private final Statement stmt;
    private final Connection connection;

    public DatabaseUser(Statement stmt, Connection connection) {
        this.stmt = stmt;
        this.connection = connection;
    }


    public boolean createUser(String username, String password) {
        if (!this.userExists(username)) {
            try (PreparedStatement registerUserStmt = this.connection.prepareStatement("INSERT INTO users (username, password) VALUES(?, ?)");) {
                registerUserStmt.setString(1, username);
                registerUserStmt.setString(2, Authentication.hashPassword(password));
                registerUserStmt.executeUpdate();

                logger.info("User was created");
                return true;
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }

        } else {
            logger.error("Username exists.");
        }
        return false;
    }


    public boolean compareExchangeToken(String username, String token) {
        try {
            if (userExists(username)) {
                ResultSet rs = stmt.executeQuery(DatabaseQuery.SELECT_TOKEN.getQuery() + username + "'");

                if (rs.next()) {
                    if (rs.getString("token").equals(token)) {
                        return true;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    public UserModel loginUser(String username, String password) {
        String exchangeToken = "mtcgToken";
        try {
            if (this.userExists(username)) {
                ResultSet rs = stmt.executeQuery(DatabaseQuery.SELECT_BY_USERNAME.getQuery() + username + "'");

                while (rs.next()) {
                    if (Authentication.passwordIsEqual(password, rs.getString("password"))) {

                        try (PreparedStatement setExchangeToken = this.connection.prepareStatement("UPDATE users SET token= ? WHERE username = ? ;");) {
                            setExchangeToken.setString(1, exchangeToken);
                            setExchangeToken.setString(2, username);
                            setExchangeToken.executeUpdate();

                            logger.info("User logged in with token: " + exchangeToken);
                            return new UserModel(
                                    rs.getString("username"),
                                    rs.getString("password"),
                                    exchangeToken,
                                    rs.getInt("balance"),
                                    new ArrayList<>(),
                                    new ArrayList<>()
                            );
                        } catch (SQLException e) {
                            logger.error(e.getMessage());
                        }
                    } else {
                        logger.error("Password for user " + username + " is incorrect.");
                    }
                }
                return null;
            } else {
                logger.error("User does not exist.");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return null;
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
