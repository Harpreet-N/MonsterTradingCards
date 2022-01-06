package http;

import database.Database;
import database.DatabaseUser;
import model.CardModel;
import model.UserModel;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;

public class ResponseHandler {
    private final Database dbA;
    private DatabaseUser db;
    private final BufferedWriter bufferedWriter;

    private static final Logger logger = Logger.getLogger(ResponseHandler.class);

    private static String HTTP_OK = "HTTP/1.1 200 OK\r\n";
    private static String HTTP_CONTENT_TYPE = "ContentType: text/html\r\n";

    public ResponseHandler(Database dbA, BufferedWriter bufferedWriter) {
        this.dbA = dbA;
        this.bufferedWriter = bufferedWriter;
        db = new DatabaseUser(dbA.getStmt(),dbA.getConnection());
    }

    public void responseOK() throws IOException {
        bufferedWriter.write(HTTP_OK);
        bufferedWriter.write(HTTP_CONTENT_TYPE);
        bufferedWriter.write("\r\nOK\r\n");
        bufferedWriter.write("\r\n");
        bufferedWriter.flush();
    }

    public void responseError() throws IOException {
        bufferedWriter.write(HTTP_OK);
        bufferedWriter.write(HTTP_CONTENT_TYPE);
        bufferedWriter.write("\r\nERROR\r\n");
        bufferedWriter.write("\r\n");
        bufferedWriter.flush();
    }

    public void responseCustom(String message) throws IOException {
        bufferedWriter.write(HTTP_OK);
        bufferedWriter.write(HTTP_CONTENT_TYPE);
        bufferedWriter.write("\r\n" + message + "\r\n");
        bufferedWriter.write("\r\n");
        bufferedWriter.flush();
    }

    public boolean showAllCards(String username) {
        try {
            bufferedWriter.write("HTTP/1.1 200 OK\r\n");
            bufferedWriter.write(("ContentType: text/html\r\n"));
            bufferedWriter.write("\r\n");

            for (CardModel c : db.getAllCards(username)) {
                bufferedWriter.write("\r\n" + c);
            }

            bufferedWriter.write("\r\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean showDeck(String username) {
        try {
            bufferedWriter.write("HTTP/1.1 200 OK\r\n");
            bufferedWriter.write("ContentType: text/html\r\n");
            bufferedWriter.write("\r\nDeck:");

            for (CardModel c : db.getDeck(username)) {
                bufferedWriter.write("\r\n" + c);
            }

            bufferedWriter.write("\r\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean showDeckPlain(String username) {
        try {
            bufferedWriter.write("HTTP/1.1 200 OK\r\n");
            bufferedWriter.write("ContentType: text/html\r\n");
            bufferedWriter.write("\r\nDeck:");

            for (CardModel c : db.getDeck(username)) {
                bufferedWriter.write("\r\n" + c.getId() + ": " + c.getElementType() + c.getMonsterType() + " has damage " + c.getDamage());
            }

            bufferedWriter.write("\r\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean getScoreboard() {
        try {
            bufferedWriter.write("HTTP/1.1 200 OK\r\n");
            bufferedWriter.write("ContentType: text/html\r\n");
            bufferedWriter.write(db.retrieveScoreboard().toString());
            bufferedWriter.write("\r\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean getStats(String username) {
        try {
            UserModel u = db.getUserData(username);
            String userData = "\r\nName: " + u.getUsername() + ", Wins: " + u.getWins() + ", Looses: " + u.getLooses() + ", Elo: " + u.getElo();

            bufferedWriter.write("HTTP/1.1 200 OK\r\n");
            bufferedWriter.write("ContentType: text/html\r\n");
            bufferedWriter.write(userData);
            bufferedWriter.write("\r\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean getUserData(String userToEdit) {
        try {
            UserModel u = db.getUserData(userToEdit);
            String userData = "\r\nName: " + u.getUsername();

            bufferedWriter.write("HTTP/1.1 200 OK\r\n");
            bufferedWriter.write("ContentType: text/html\r\n");
            bufferedWriter.write(userData);
            bufferedWriter.write("\r\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean getTradingDeals() {
        try {
            bufferedWriter.write("HTTP/1.1 200 OK\r\n");
            bufferedWriter.write("ContentType: text/html\r\n");
            bufferedWriter.write(db.retrieveAllTradingDeals().toString());
            bufferedWriter.write("\r\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }


}
