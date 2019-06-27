package me.cbitler.raidbot.database.sqlite;

import lombok.Getter;
import me.cbitler.raidbot.database.sqlite.dao.*;

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
        this.sqliteDatabaseDAO = new SqliteDatabaseDAOImpl("GW2-raid-bot.db");
        sqliteDatabaseDAO.connect();

        initializeDao();
    }

    private void initializeDao() {
        raidDao = new RaidDao(sqliteDatabaseDAO.getConnection());
        serverSettingsDao = new ServerSettingsDao(sqliteDatabaseDAO.getConnection());
        usersDao = new UsersDao(sqliteDatabaseDAO.getConnection());
        usersFlexRolesDao = new UsersFlexRolesDao(sqliteDatabaseDAO.getConnection());
    }
}
