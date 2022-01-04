package database;

public enum DatabaseQuery {
    // CREATE
    USER_QUERY("CREATE TABLE users (username VARCHAR ( 255 ) PRIMARY KEY, password VARCHAR ( 255 ) NOT NULL,  balance INTEGER DEFAULT 20, stackmodel VARCHAR ( 255 ), deck VARCHAR ( 255 )) "),
    CARD_QUERY("CREATE TABLE cards (uuid VARCHAR ( 255 ) PRIMARY KEY, name VARCHAR ( 255 ) NULL, damage DECIMAL(5,2) NOT NULL, elementType VARCHAR ( 255 ) NOT NULL,  monsterType VARCHAR ( 255 ) NOT NULL, stackModel VARCHAR ( 255 ) NULL, deck VARCHAR ( 255 ) NULL)"),
    STORE_QUERY("CREATE TABLE store (uuid VARCHAR ( 255 ) PRIMARY KEY, offerer VARCHAR ( 255 ) NOT NULL, card_to_trade VARCHAR ( 255 ) NOT NULL, wants_monster BOOLEAN DEFAULT FALSE, wants_spell BOOLEAN DEFAULT FALSE, min_damage INTEGER DEFAULT 0)"),

    // DROP
    DROP_ALL_TABLES("DROP TABLE IF EXISTS users, cards, store;");



    private final String query;

    DatabaseQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
