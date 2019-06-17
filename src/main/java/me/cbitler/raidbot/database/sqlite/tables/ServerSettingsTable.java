package me.cbitler.raidbot.database.sqlite.tables;

public class ServerSettingsTable {

    private ServerSettingsTable() {
    }

    private static final String TABLE_NAME = "serverSettings";
    private static final String SERVER_ID = "serverId";
    private static final String RAID_LEADER_ROLE = "raid_leader_role";

    public static final String SERVER_SETTINGS_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (\n"
            + SERVER_ID + " text PRIMARY KEY, \n"
            + RAID_LEADER_ROLE + " text)";
}
