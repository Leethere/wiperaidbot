package me.cbitler.raidbot.database.sqlite.dao;

import me.cbitler.raidbot.database.sqlite.tables.RaidTable;
import me.cbitler.raidbot.database.sqlite.tables.ServerSettingsTable;
import me.cbitler.raidbot.database.sqlite.tables.UserFlexRoleTable;
import me.cbitler.raidbot.database.sqlite.tables.UserTable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for managing the SQLite database for this bot
 * @author Christopher Bitler
 */
public class SqliteDatabaseDAOImpl extends BaseFunctionality {
    private String databaseName;

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
