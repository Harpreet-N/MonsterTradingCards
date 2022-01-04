package server;

import database.Database;
import database.DatabaseService;

public class Main {

    public static void main(String[] args) {
        // Create Database
        final Database db = new Database();
        db.connect();

        final DatabaseService databaseService = new DatabaseService(db.getStmt(), db.getConnection());
        databaseService.createTable();
    }
}
