package http;

import database.Database;
import database.DatabaseStore;
import database.DatabaseUser;
import model.CardModel;
import model.UserModel;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;

public class ResponseHandler {
    private final Database db;
    private DatabaseUser databaseUser;
    private DatabaseStore databaseStore;
    private final BufferedWriter bufferedWriter;

    private static final Logger logger = Logger.getLogger(ResponseHandler.class);

    private static String HTTP_OK = "HTTP/1.1 200 OK\r\n";
    private static String HTTP_CONTENT_TYPE = "ContentType: text/html\r\n";

    public ResponseHandler(Database db, BufferedWriter bufferedWriter) {
        this.db = db;
        this.bufferedWriter = bufferedWriter;
        databaseUser = new DatabaseUser(db.getStmt(), db.getConnection());
        databaseStore = new DatabaseStore(db.getStmt(), db.getConnection());
    }

    public void response(String message) {
        try {
            bufferedWriter.write(HTTP_OK);
            bufferedWriter.write(HTTP_CONTENT_TYPE);
            bufferedWriter.write("\r\n" + message + "\r\n");
            bufferedWriter.write("\r\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }


    public boolean showAllCards(String username) {
        try {
            bufferedWriter.write(HTTP_OK);
            bufferedWriter.write((HTTP_CONTENT_TYPE));
            bufferedWriter.write("\r\n");

            for (CardModel c : databaseUser.getAllCards(username)) {
                bufferedWriter.write("\r\n" + c);
            }

            closeBuffer();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean showDeck(String username) {
        try {
            bufferedWriter.write(HTTP_OK);
            bufferedWriter.write(HTTP_CONTENT_TYPE);
            bufferedWriter.write("\r\nDeck:");

            for (CardModel c : databaseUser.getDeck(username)) {
                bufferedWriter.write("\r\n" + c);
            }

            closeBuffer();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean showDeckPlain(String username) {
        try {
            bufferedWriter.write(HTTP_OK);
            bufferedWriter.write(HTTP_CONTENT_TYPE);
            bufferedWriter.write("\r\nDeck:");

            for (CardModel c : databaseUser.getDeck(username)) {
                bufferedWriter.write("\r\n" + c.getId() + ": " + c.getElementType() + c.getMonsterType() + " has damage " + c.getDamage());
            }

            closeBuffer();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean getScoreboard() {
        try {
            bufferedWriter.write(HTTP_OK);
            bufferedWriter.write(HTTP_CONTENT_TYPE);
            bufferedWriter.write(databaseUser.retrieveScoreboard().toString());
            closeBuffer();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean getStats(String username) {
        try {
            UserModel u = databaseUser.getUserData(username);
            String userData = "\r\nName: " + u.getUsername() + ", Wins: " + u.getWins() + ", Looses: " + u.getLooses() + ", Elo: " + u.getElo();

            bufferedWriter.write(HTTP_OK);
            bufferedWriter.write(HTTP_CONTENT_TYPE);
            bufferedWriter.write(userData);
            closeBuffer();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean getUserData(String userToEdit) {
        try {
            UserModel u = databaseUser.getUserData(userToEdit);
            String userData = "\r\nName: " + u.getName() + ", Bio: " + u.getBio() + ", Image: " + u.getImage();

            bufferedWriter.write(HTTP_OK);
            bufferedWriter.write(HTTP_CONTENT_TYPE);
            bufferedWriter.write(userData);
            closeBuffer();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean getTradingDeals() {
        try {
            bufferedWriter.write(HTTP_OK);
            bufferedWriter.write(HTTP_CONTENT_TYPE);
            bufferedWriter.write(databaseStore.retrieveAllTrads().toString());
            closeBuffer();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    private void closeBuffer() {
        try {
            bufferedWriter.write("\r\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
