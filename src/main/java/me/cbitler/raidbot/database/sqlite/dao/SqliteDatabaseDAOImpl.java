package me.cbitler.raidbot.database.sqlite.dao;

import java.sql.Connection;

/**
 * Class for managing the SQLite database for this bot
 * @author Christopher Bitler
 */
public class SqliteDatabaseDAOImpl extends BaseFunctionality {

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
