package server;

import database.Database;
import database.DatabaseService;
import http.RequestHandler;
import logic.BattleLogic;
import org.apache.log4j.Logger;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args){
        // Create Database
        final Database db = new Database();
        db.connect();

        final DatabaseService databaseService = new DatabaseService(db.getStmt());
        databaseService.createTable();

        // Init battle logic
        final BattleLogic battleLogic = new BattleLogic(db);

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
