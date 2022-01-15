package database;

public enum DatabaseQuery {
    // CREATE
    USER_QUERY("CREATE TABLE users (username VARCHAR ( 255 ) PRIMARY KEY, name VARCHAR ( 255 ), password VARCHAR ( 255 ) NOT NULL, token VARCHAR ( 255 ), bio VARCHAR ( 255 ), image VARCHAR ( 255 ), deck VARCHAR ( 255 ), stackModel VARCHAR ( 255 ), balance INTEGER DEFAULT 20, elo INTEGER DEFAULT 100, wins INTEGER DEFAULT 0, looses INTEGER DEFAULT 0)"),
    CARD_QUERY("CREATE TABLE cards (uuid VARCHAR ( 255 ) PRIMARY KEY, owner VARCHAR ( 255 ) NULL, packageId VARCHAR ( 255 ) NULL, cardtype VARCHAR ( 255 ) NOT NULL, elementtype VARCHAR ( 255 ) NOT NULL, storagetype VARCHAR ( 255 ) NOT NULL, damage DECIMAL(5,2) NOT NULL, locked BOOLEAN DEFAULT FALSE)"),
    STORE_QUERY("CREATE TABLE store (uuid VARCHAR ( 255 ) PRIMARY KEY, offerer VARCHAR ( 255 ) NOT NULL, card_to_trade VARCHAR ( 255 ) NOT NULL, wants_monster BOOLEAN DEFAULT FALSE, wants_spell BOOLEAN DEFAULT FALSE, min_damage INTEGER DEFAULT 0)"),

    // DROP
    DROP_ALL_TABLES("DROP TABLE IF EXISTS users, cards, store;"),

    SELECT_TOKEN("SELECT token FROM users WHERE username='"),
    SELECT_BY_USERNAME("SELECT * FROM users WHERE username='"),
    SELECT_USERNAME("SELECT username FROM users"),

    //Select
    SELECT_ALL_CARDS("SELECT * FROM cards"),
    SELECT_LOCKED_CARD("SELECT locked FROM cards WHERE UUID='"),
    SELECT_FETCH_DECK_BY_USER("SELECT * FROM cards WHERE storagetype='deck' AND owner='"),
    SELECT_FETCH_STACK_BY_USER("SELECT * FROM cards WHERE storagetype='stack' AND owner= ?"),
    SELECT_LATEST_PACKAGE("SELECT packageId FROM cards WHERE storagetype='package' LIMIT 1"),
    SELECT_ORDER_USERS_BY_ELO("SELECT username, elo, wins, looses FROM users ORDER BY elo DESC"),
    SELECT_STORAGE_BY_UUID("SELECT storagetype FROM cards WHERE uuid= ?"),
    SELECT_ALL_TRADING_DEALS("SELECT * FROM store"),
    SELECT_CARD_BY_UUID("SELECT * FROM cards WHERE uuid=?"),
    SELECT_TRADE_WHERE_UUID("SELECT * FROM store WHERE uuid='"),

    // Delete
    DELETE_TRADE_BY_UUID("DELETE FROM store WHERE uuid='"),

    // Elo
    SELECT_FETCH_COINS("SELECT balance FROM users WHERE username= ?"),
    SELECT_FETCH_ELO("SELECT elo FROM users WHERE username='");


    private final String query;

    DatabaseQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
