package me.cbitler.raidbot.database.sqlite;

import lombok.Getter;
import me.cbitler.raidbot.database.sqlite.dao.RaidDao;

public class SqliteDAL {
    private static SqliteDAL instance = null;

    @Getter
    private SqliteDatabaseDAOImpl sqliteDatabaseDAO;
    @Getter
    private RaidDao raidDao;

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
    }
}
