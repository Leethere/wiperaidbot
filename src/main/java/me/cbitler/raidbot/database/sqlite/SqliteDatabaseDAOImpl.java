package me.cbitler.raidbot.database.sqlite;

import me.cbitler.raidbot.database.QueryResult;
import me.cbitler.raidbot.database.sqlite.tables.RaidTable;
import me.cbitler.raidbot.database.sqlite.tables.ServerSettingsTable;
import me.cbitler.raidbot.database.sqlite.tables.UserFlexRoleTable;
import me.cbitler.raidbot.database.sqlite.tables.UserTable;

import java.sql.*;

/**
 * Class for managing the SQLite database for this bot
 * @author Christopher Bitler
 */
public class SqliteDatabaseDAOImpl {
    String databaseName;
    Connection connection;

    /**
     * Create a new database with the specific filename
     * @param databaseName The filename/location of the SQLite database
     */
    public SqliteDatabaseDAOImpl(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * Connect to the SQLite database and create the tables if they don't exist
     */
    public void connect() {
        String url = "jdbc:sqlite:" + databaseName;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("SqliteDatabaseDAOImpl connection error");
            System.exit(1);
        }

        try {
            tableInits(connection);
        } catch (SQLException e) {
            System.out.println("Couldn't create tables");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Run a query and return the results using the specified query and parameters
     * @param query The query with ?s where the parameters need to be placed
     * @param data The parameters to put in the query
     * @return QueryResult representing the statement used and the ResultSet
     * @throws SQLException
     */
    public QueryResult query(String query, String[] data) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);
        int i = 1;
        for(String input : data) {
            stmt.setObject(i, input);
            i++;
        }

        ResultSet rs = stmt.executeQuery();

        return new QueryResult(stmt, rs);
    }

    /**
     * Run an update query with the specified parameters
     * @param query The query with ?s where the parameters need to be placed
     * @param data The parameters to put in the query
     * @throws SQLException
     */
    public void update(String query, String[] data) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);
        int i = 1;
        for(String input : data) {
            stmt.setObject(i, input);
            i++;
        }

        stmt.execute();
        stmt.close();
    }

//    /**
//     * Create the database tables. Also alters the raid table to add the leader column if it doesn't exist.
//     * @throws SQLException
//     */
//    public void tableInits() throws SQLException {
//        connection.createStatement().execute(raidTableInit);
//        connection.createStatement().execute(raidUsersTableInit);
//        connection.createStatement().execute(raidUsersFlexRolesTableInit);
//        connection.createStatement().execute(botServerSettingsInit);
//
//        try {
//        	connection.createStatement().execute("ALTER TABLE raids ADD COLUMN leader text");
//        } catch (Exception e) { }
//        try {
//        	connection.createStatement().execute("ALTER TABLE raids ADD COLUMN `description` text");
//        } catch (Exception e) { }
//        try {
//        	connection.createStatement().execute("ALTER TABLE raids ADD COLUMN isOpenWorld text");
//        } catch (Exception e) { }
//    }

    /**
     * Create the database tables
     *
     * @throws SQLException
     */
    private static void tableInits(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.addBatch(RaidTable.RAID_TABLE_CREATE);
        statement.addBatch(UserTable.USERS_TABLE_CREATE);
        statement.addBatch(UserFlexRoleTable.USERS_FLEX_ROLES_TABLE_CREATE);
        statement.addBatch(ServerSettingsTable.SERVER_SETTINGS_CREATE);

        statement.executeBatch();
    }
}
