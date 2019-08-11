package me.cbitler.raidbot.database.sqlite;

import lombok.Getter;
import me.cbitler.raidbot.database.sqlite.dao.*;
import me.cbitler.raidbot.database.sqlite.tables.RaidTable;
import me.cbitler.raidbot.database.sqlite.tables.ServerSettingsTable;
import me.cbitler.raidbot.database.sqlite.tables.UserFlexRoleTable;
import me.cbitler.raidbot.database.sqlite.tables.UserTable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Getter
public class SqliteDAL {
    private static SqliteDAL instance = null;

    private SqliteDatabaseDAOImpl sqliteDatabaseDAO;
    private RaidDao raidDao;
    private ServerSettingsDao serverSettingsDao;
    private UsersDao usersDao;
    private UsersFlexRolesDao usersFlexRolesDao;

    public static synchronized SqliteDAL getInstance() {
        if (instance == null) {
            instance = new SqliteDAL();
            return instance;
        }
        return instance;
    }

    private SqliteDAL() {
        Connection connection = connect("GW2-raid-bot.db");
        initDatabaseTables(connection);

        sqliteDatabaseDAO = new SqliteDatabaseDAOImpl();
        sqliteDatabaseDAO.setConnection(connection);

        initializeDao();
    }

    private void initializeDao() {
        raidDao = new RaidDao(sqliteDatabaseDAO.getConnection());
        serverSettingsDao = new ServerSettingsDao(sqliteDatabaseDAO.getConnection());
        usersDao = new UsersDao(sqliteDatabaseDAO.getConnection());
        usersFlexRolesDao = new UsersFlexRolesDao(sqliteDatabaseDAO.getConnection());
    }

    /**
     * Connect to the SQLite database and create the tables if they don't exist
     */
    private Connection connect(String databaseName) {
        String url = "jdbc:sqlite:" + databaseName;
        try {
            Connection connection;
            connection = DriverManager.getConnection(url);
            return connection;
        } catch (SQLException e) {
            System.out.println("SqliteDatabaseDAOImpl connection error");
            System.exit(1);
        }
        //This should be unreachable as the program will exit if the connection is able to be made
        return null;
    }

    /**
     * Create the database tables
     */
    private static void initDatabaseTables(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.addBatch(RaidTable.RAID_TABLE_CREATE);
            statement.addBatch(UserTable.USERS_TABLE_CREATE);
            statement.addBatch(UserFlexRoleTable.USERS_FLEX_ROLES_TABLE_CREATE);
            statement.addBatch(ServerSettingsTable.SERVER_SETTINGS_CREATE);

            statement.executeBatch();
        } catch (SQLException e) {
            System.out.println("Couldn't create tables");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
