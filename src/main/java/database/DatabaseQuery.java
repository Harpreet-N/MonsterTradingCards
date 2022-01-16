package database;

public enum DatabaseQuery {

    SELECT_TOKEN("SELECT token FROM users WHERE username='"),
    SELECT_BY_USERNAME("SELECT * FROM users WHERE username='"),
    SELECT_USERNAME("SELECT username FROM users"),

    //Select
    SELECT_ALL_CARDS("SELECT * FROM cards"),
    SELECT_LOCKED_CARD("SELECT locked FROM cards WHERE UUID='"),
    SELECT_FETCH_DECK_BY_USER("SELECT * FROM cards WHERE storagetype='deck' AND owner='"),
    SELECT_FETCH_STACK_BY_USER("SELECT * FROM cards WHERE storagetype='stack' AND owner= ?"),
    SELECT_LATEST_PACKAGE("SELECT packageId FROM cards WHERE storagetype='package' LIMIT 1"),
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
