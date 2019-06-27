package me.cbitler.raidbot.database.sqlite.dao;

import java.sql.Connection;

public class ServerSettingsDao extends BaseFunctionality {
    public ServerSettingsDao(Connection connection) {
        this.connection = connection;
    }
}
