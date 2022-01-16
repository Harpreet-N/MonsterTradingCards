package database;

public enum DatabaseQuery {

    SELECT_BY_USERNAME("SELECT * FROM users WHERE username='"),
    SELECT_ALL_CARDS("SELECT * FROM cards"),
    SELECT_ALL_TRADING_DEALS("SELECT * FROM store"),
    SELECT_TRADE_WHERE_UUID("SELECT * FROM store WHERE uuid='");

    private final String query;

    DatabaseQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
