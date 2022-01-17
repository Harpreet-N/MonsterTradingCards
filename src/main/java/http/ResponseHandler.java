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
    private Database db;
    private DatabaseUser databaseUser;
    private DatabaseStore databaseStore;
    private final BufferedWriter bufferedWriter;

    private static final Logger logger = Logger.getLogger(ResponseHandler.class);

    private static String HTTP_OK = "HTTP/1.1 200 OK";
    public static final String LINE_END = "\r\n";
    private static String HTTP_CONTENT_TYPE_JSON = "Content-Type: application/json";

    public ResponseHandler(Database db, BufferedWriter bufferedWriter) {
        this.db = db;
        this.bufferedWriter = bufferedWriter;
        databaseUser = new DatabaseUser(db.getStmt(), db.getConnection());
        databaseStore = new DatabaseStore(db.getStmt(), db.getConnection());
    }

    public void response(String message) {
        try {
            bufferedWriter.write(HTTP_OK + LINE_END);
            bufferedWriter.write(HTTP_CONTENT_TYPE_JSON + LINE_END);
            bufferedWriter.write("Content-Length: " + message.length() + LINE_END);
            bufferedWriter.write(LINE_END);
            bufferedWriter.write(message + LINE_END);
            bufferedWriter.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }


    public boolean respondsAllCard(String username) {
        try {
            bufferedWriter.write(HTTP_OK + LINE_END);
            bufferedWriter.write(HTTP_CONTENT_TYPE_JSON + LINE_END);

            for (CardModel c : databaseUser.getAllCards(username)) {
                bufferedWriter.write("\r\n" + c);
            }
            bufferedWriter.flush();

            closeBuffer();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean respondDeck(String username) {
        try {
            bufferedWriter.write(HTTP_OK + LINE_END);
            bufferedWriter.write(HTTP_CONTENT_TYPE_JSON + LINE_END);
            bufferedWriter.write("Content-Length: " + LINE_END);
            bufferedWriter.write(LINE_END);
            bufferedWriter.write("Deck" + LINE_END);
            for (CardModel model : databaseUser.getDeck(username)) {
                bufferedWriter.write("\r\n" + model);
            }
            closeBuffer();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean respondPlainDeck(String username) {
        try {
            bufferedWriter.write(HTTP_OK + LINE_END);
            bufferedWriter.write(HTTP_CONTENT_TYPE_JSON + LINE_END);
            bufferedWriter.write(LINE_END);
            bufferedWriter.write("Deck" + LINE_END);

            for (CardModel c : databaseUser.getDeck(username)) {
                bufferedWriter.write(LINE_END + c.getId() + ": " + c.getElementType() + c.getMonsterType() + " has the damage " + c.getDamage());
            }
            closeBuffer();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean respondScore() {
        try {
            bufferedWriter.write(HTTP_OK + LINE_END);
            bufferedWriter.write(HTTP_CONTENT_TYPE_JSON + LINE_END);
            bufferedWriter.write(LINE_END);
            bufferedWriter.write(databaseUser.getRank().toString());
            closeBuffer();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean respondStats(String username) {
        try {
            bufferedWriter.write(HTTP_OK + LINE_END);
            bufferedWriter.write(HTTP_CONTENT_TYPE_JSON + LINE_END);
            UserModel u = databaseUser.getUserData(username);
            String userData = LINE_END + "Name: " + u.getUsername() + ", Wins: " + u.getWins() + ", Lost: " + u.getLooses() + ", Elo: " + u.getElo();
            bufferedWriter.write(HTTP_OK);

            bufferedWriter.write(userData);
            closeBuffer();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean respondUserData(String userToEdit) {
        try {
            bufferedWriter.write(HTTP_OK + LINE_END);
            bufferedWriter.write(HTTP_CONTENT_TYPE_JSON + LINE_END);

            UserModel u = databaseUser.getUserData(userToEdit);
            String userData = LINE_END + "Name: " + u.getUsername() + ", Bio: " + u.getBio() + ", Image: " + u.getImage();

            bufferedWriter.write(userData);
            closeBuffer();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean respondTrade() {
        try {
            bufferedWriter.write(HTTP_OK + LINE_END);
            bufferedWriter.write(HTTP_CONTENT_TYPE_JSON + LINE_END);
            bufferedWriter.write(databaseStore.getAllTrade().toString());
            closeBuffer();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    private void closeBuffer() {
        try {
            bufferedWriter.write(LINE_END);
            bufferedWriter.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
