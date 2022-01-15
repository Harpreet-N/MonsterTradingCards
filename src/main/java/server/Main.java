package server;

import database.Database;
import database.DatabaseService;
import database.DatabaseStore;
import database.DatabaseUser;
import http.RequestHandler;
import logic.BattleLogic;
import org.apache.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Statement;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);
    private static Connection connection;
    private static Statement stmt;


    public static void main(String[] args) {
        // Create Database
        final Database db = new Database();
        db.connect();

        connection = db.getConnection();
        stmt = db.getStmt();

        // Create DatabaseService
        final DatabaseService databaseService = new DatabaseService(stmt);
        databaseService.createTable();

        // Create DatabaseStore
        final DatabaseStore databaseStore = new DatabaseStore(stmt, connection);

        // Create DatabaseUser
        final DatabaseUser databaseUser = new DatabaseUser(stmt, connection);

        // Create BattleLogic
        final BattleLogic battleLogic = new BattleLogic(db);

        try (ServerSocket serverSocket = new ServerSocket(10001, 5)) {
            logger.info("Sever started...");
            while (true) {
                final Socket clientSocket = serverSocket.accept();
                final RequestHandler requestHandler = new RequestHandler(db, databaseUser, databaseStore, clientSocket, battleLogic);

                requestHandler.start();
            }
        } catch (Exception e) {
            logger.error("Server stopped: " + e.getMessage());
        }
    }
}
