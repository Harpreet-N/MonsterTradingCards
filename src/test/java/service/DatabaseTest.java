package service;

import database.Database;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DatabaseTest {

    private Database database = new Database();


    @Test
    void dbConnectionTest() {
        database.connect();
        assertNotNull(database.getConnection());
    }

}
