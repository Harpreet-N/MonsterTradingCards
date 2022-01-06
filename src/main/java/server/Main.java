package server;

import database.Database;
import database.DatabaseService;
import database.DatabaseUser;
import http.RequestHandler;
import http.ResponseHandler;
import logic.BattleLogic;
import org.apache.log4j.Logger;

import javax.xml.crypto.Data;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {


    private static final Logger logger = Logger.getLogger(ResponseHandler.class);

    public static void main(String[] args) {
        // Create Database
        final Database db = new Database();
        db.connect();

        final DatabaseService databaseService = new DatabaseService(db.getStmt(), db.getConnection());
        databaseService.createTable();

        final DatabaseUser databaseUser = new DatabaseUser(db.getStmt(), db.getConnection());

        // Init battle logic
        final BattleLogic battleLogic = new BattleLogic(db);

        // Init thread pool
        final ExecutorService executorService = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(10001, 5)) {
            logger.info("Sever started...");
            while (true) {
                final Socket clientSocket = serverSocket.accept();
                final RequestHandler requestHandler = new RequestHandler(db, clientSocket, battleLogic);

                requestHandler.start();
            }
        } catch (Exception e) {
            logger.error("Server stopped: " + e.getMessage());
        }
    }
}
